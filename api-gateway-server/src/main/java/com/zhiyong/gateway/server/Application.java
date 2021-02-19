package com.zhiyong.gateway.server;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.zhiyong.gateway")
@MapperScan("com.zhiyong.gateway.dal.dao")
@DubboComponentScan(value = {"com.zhiyong.gateway.biz.facade"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
