package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.config.TelegramBotConfig;
import com.example.scrumhelp.scrum.service.ScrumHelpBotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class ScrumHelpBot extends TelegramLongPollingBot {
    private final ScrumHelpBotService scrumHelpBotService;
    private final TelegramBotConfig telegramBotConfig;

    @Autowired
    public ScrumHelpBot(ScrumHelpBotService scrumHelpBotService,
                        TelegramBotConfig telegramBotConfig) {
        this.scrumHelpBotService = scrumHelpBotService;
        this.telegramBotConfig = telegramBotConfig;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println("Received message from user " +
                update.getMessage().getFrom().getUserName() +
                " with text: " + update.getMessage().getText());

        if ("/register" .equals(update.getMessage().getText())) {
            Long chatId = update.getMessage().getChatId();

            String responseMessage = scrumHelpBotService.registerUser(chatId,
                    update.getMessage().getFrom());
            try {
                execute(new SendMessage(chatId.toString(), responseMessage));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else if ("/setDailyReminder".equals(update.getMessage().getText())) {
            //TODO: setDailyReminder
        }
    }

    @Override
    public String getBotToken() {
        return telegramBotConfig.getBotToken();
    }

    @Override
    public String getBotUsername() {
        return telegramBotConfig.getBotUserName();
    }
}



