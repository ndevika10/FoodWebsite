package com.example.FoodWebsite.configuration;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

@Configuration
public class EhCacheConfig {

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            cm.createCache("cartCache", new MutableConfiguration<>()
                    .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES))
                    .setStoreByValue(false)
                    .setStatisticsEnabled(true));
        };
    }
}

