package com.leyou.auth.client;


import com.leyou.user.pojo.com.leyou.user.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("user-service")
public interface UserClient extends UserApi {
}
