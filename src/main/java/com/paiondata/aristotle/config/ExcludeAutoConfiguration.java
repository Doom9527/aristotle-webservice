package com.paiondata.aristotle.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * Exclude auto-configurations at startup.
 * This configuration takes effect when the value of the property "spring.redis.enabled" is false,
 * which prevents the application from loading RedissonAutoConfiguration.class and RedisAutoConfiguration.class.
 */
@Configuration
@ConditionalOnProperty(value = "spring.redis.enabled", havingValue = "false")
@EnableAutoConfiguration(exclude = {RedisAutoConfiguration.class})
public class ExcludeAutoConfiguration {
}
