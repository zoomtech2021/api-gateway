package com.zhiyong.gateway.biz.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource(locations = {"classpath:elasticjob-config.xml"})
public class ElasticJobConfig {
}
