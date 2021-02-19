package com.zhiyong.gateway.admin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @ClassName RedisSessionConfig
 * @Description: TODO
 * @Author 毛军锐
 * @Date 2020/12/1 下午1:44
 **/
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {
}
