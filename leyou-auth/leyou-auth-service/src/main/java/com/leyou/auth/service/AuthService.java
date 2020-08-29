package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserClient userClient;
    @Autowired
    private JwtProperties properties;

    public String accredit(String username, String password) {

        try {
            //根据用户名和密码查询是否存在该用户
            User user = this.userClient.query(username, password);
            //判断
            if (user == null) {
                return null;
            }
            //若存在，则生成token
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), properties.getPrivateKey(), properties.getExpire());
            return token;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
