package com.leyou.auth.controller;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.service.AuthService;
import com.leyou.auth.config.JwtProperties;


import com.leyou.auth.utils.JwtUtils;
import com.leyou.smsutils.CookieUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@EnableConfigurationProperties(JwtProperties.class)
@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private JwtProperties properties;
    /**
     * - 请求方式：post
     * - 请求路径：/accredit
     * - 请求参数：username和password
     * - 返回结果：无
     */

    @PostMapping("accredit")
    public ResponseEntity<Void> accredit(@RequestParam("username")String username, @RequestParam("password")String password,
                                         HttpServletRequest request, HttpServletResponse response){

        //登录校验
        String token = this.authService.accredit(username,password);

        //判断token
        if(StringUtils.isBlank(token)){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //设置cookie
        CookieUtils.setCookie(request,response,properties.getCookieName(),token,properties.getCookieMaxAge()*60,null);

        return ResponseEntity.ok().build();
    }

    /**
     * 登录后页面回显用户信息
     * Request URL: http://api.leyou.com/api/auth/verify
     * Request Method: GET
     * 传入的参数为cookie中保存的数据
     *
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo>verify(@CookieValue("LY_TOKEN")String token,
                                          HttpServletRequest request, HttpServletResponse response) {
        try {
            //将页面上传入的cookie使用公钥进行解密，得到用户得信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, properties.getPublicKey());

            if (userInfo == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //问题：若用户一直在使用网站，cookie的有效时间不会减，用户体验更友好
            //但是，工具类没有提供刷新时间的方法，我们可以重新生成cookie和token保证它在使用时不会过期

            //刷新token
            token = JwtUtils.generateToken(userInfo,this.properties.getPrivateKey(),this.properties.getExpire());

            //刷新cookie
            CookieUtils.setCookie(request,response,this.properties.getCookieName(),token,this.properties.getCookieMaxAge()*60);

            return ResponseEntity.ok(userInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



}
