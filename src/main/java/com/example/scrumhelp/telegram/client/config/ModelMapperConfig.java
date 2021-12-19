package com.example.scrumhelp.telegram.client.config;

import eye2web.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
