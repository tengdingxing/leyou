package com.leyou.gateway.config;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.smsutils.CookieUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class,AllowPathsProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties properties;
    @Autowired
    private AllowPathsProperties allowPathsProperties;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        //获取请求url，请求url是带有域名的
        StringBuffer requestURL = request.getRequestURL();
        //获取白名单
        List<String> allowPaths = this.allowPathsProperties.getAllowPaths();
        //判断请求url中是否包含域名
        for (String allowPath:allowPaths) {
            StringUtils.contains(requestURL,allowPath);

            return false;
        }

        return true;
    }

    @Override
    public Object run() throws ZuulException {

        //通过zuul网关获取，zuul的运行时上下文，里面有request的传递参数的方式
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        //获取cookie中的token,通过JWT对token进行校验,通过：则放行；不通过：则重定向到登录页
        String token = CookieUtils.getCookieValue(request,this.properties.getCookieName());

        //如果为空，拦截
        if (token == null){
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        try {
            //如果不为空，进行校验
            JwtUtils.getInfoFromToken(token, this.properties.getPublicKey());
        }catch (Exception e){
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
