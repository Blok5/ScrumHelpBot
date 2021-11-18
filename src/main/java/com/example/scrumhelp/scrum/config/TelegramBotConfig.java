package com.example.scrumhelp.scrum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotConfig {
    private String botToken;
    private String botUserName;

    public String getBotToken() {
        return botToken;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    public String getBotUserName() {
        return botUserName;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    @Override
    public String toString() {
        return "TelegramConfig{" +
                "botToken='" + botToken + '\'' +
                ", botUserName='" + botUserName + '\'' +
                '}';
    }
}
