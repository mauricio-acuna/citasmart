package com.citasmart.userservice.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis cache configuration
 * 
 * @author CitaSmart Team
 * @version 1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Redis cache manager configuration
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // Default TTL: 1 hour
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Specific cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // User cache - 30 minutes TTL
        cacheConfigurations.put("users", defaultConfig
                .entryTtl(Duration.ofMinutes(30)));
        
        // User stats cache - 5 minutes TTL (frequently changing data)
        cacheConfigurations.put("userStats", defaultConfig
                .entryTtl(Duration.ofMinutes(5)));
        
        // JWT token cache - 24 hours TTL
        cacheConfigurations.put("jwtTokens", defaultConfig
                .entryTtl(Duration.ofHours(24)));
        
        // Email verification cache - 24 hours TTL
        cacheConfigurations.put("emailVerification", defaultConfig
                .entryTtl(Duration.ofHours(24)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
