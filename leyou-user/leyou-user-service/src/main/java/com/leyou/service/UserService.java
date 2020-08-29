package com.leyou.service;

import com.leyou.mapper.UserMapper;
import com.leyou.smsutils.NumberUtils;
import com.leyou.user.pojo.User;
import com.leyou.utils.CodecUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final String KEY_PREFIX ="user:code:phone:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    public Boolean checkData(String data, Integer type) {
        User user = new User();

        switch (type){
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(user) == 0;
    }

    public Boolean sendVerifyCode(String phone) {

        try {
            //生成验证码
            String code = NumberUtils.generateCode(6);

            //发送短信rabbitmq
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE", "sms.verify.code", msg);

            //将验证码存入redis中，时长为5分钟
            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        }catch (Exception e){
            return false;
        }

    }

    /**
     * 用户注册功能
     * */
    public void register(User user, String code) {

        //判断验证码是否正确
        //查询redis中的验证码
        String redisCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code,redisCode)){
            return;
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));

        //新增一个用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);
    }

    public User query(String username, String password) {

        //通过用户名查询用户
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);

        //判断是否存在该用户
        if(user == null){
            return null;
        }

        //先将请求参数传入的密码加盐加密进行，之后再与用户查询的密码进行比较
        password = CodecUtils.md5Hex(password, user.getSalt());

        if (!StringUtils.equals(password,user.getPassword())){
            return null;
        }

        return user;

    }
}
