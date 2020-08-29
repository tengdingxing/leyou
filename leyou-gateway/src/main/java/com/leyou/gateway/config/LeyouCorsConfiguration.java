package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许携带任何请求头
        corsConfiguration.addAllowedHeader("*");
        //允许任何请求方法
        corsConfiguration.addAllowedMethod("*");
        //设置跨域的请求信息
        corsConfiguration.addAllowedOrigin("http://www.leyou.com");
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");
        //是否允许携带cookie
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }
}
