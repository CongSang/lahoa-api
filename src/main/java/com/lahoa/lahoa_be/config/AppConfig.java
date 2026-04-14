package com.lahoa.lahoa_be.config;

import com.lahoa.lahoa_be.util.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${snowflake.worker-id}")
    private long workerId;

    @Value("${snowflake.datacenter-id}")
    private long datacenterId;

    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator() {
        return new SnowflakeIdGenerator(workerId, datacenterId);
    }
}
