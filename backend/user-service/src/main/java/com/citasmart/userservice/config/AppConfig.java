package com.citasmart.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Application configuration
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Configuration
@EnableAsync
@EnableScheduling
public class AppConfig {
    // Additional configuration beans can be added here
}
