package com.example.scrumhelp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("com.example.scrumhelp.telegram.client.config")
public class ScrumHelpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScrumHelpApplication.class, args);
    }

}
