package com.example.scrumhelp.scrum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Configuration
public class ScheduledFutureStoreComponentConfig {

    @Bean
    public Map<Long, ScheduledFuture<?>> scheduledFutureMap(){
        return new HashMap<>();
    }
}
