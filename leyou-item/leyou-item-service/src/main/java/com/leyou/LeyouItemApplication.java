package com.leyou;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;


@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper")//扫描接口所在的包
@EnableTransactionManagement //使用事务要开启事务注解

public class LeyouItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouItemApplication.class,args);

}
}
