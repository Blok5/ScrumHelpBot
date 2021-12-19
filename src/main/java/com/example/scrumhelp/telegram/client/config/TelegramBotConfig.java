package com.example.scrumhelp.telegram.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "telegram.bot")
@Configuration
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
        return "TelegramBotConfig{" +
                "botToken='" + botToken + '\'' +
                ", botUserName='" + botUserName + '\'' +
                '}';
    }
}
