package com.example.scrumhelp.telegram.client.service;

import com.example.scrumhelp.telegram.client.component.ScheduledFutureStoreComponent;
import com.example.scrumhelp.telegram.client.config.TelegramBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.TimeZone;

import static com.example.scrumhelp.telegram.client.enums.DailyReminderState.*;

@Service
@EnableScheduling
@Slf4j
public class ScrumHelpBot extends TelegramLongPollingBot {
    private final ScrumHelpBotService scrumHelpBotService;
    private final TelegramBotConfig telegramBotConfig;
    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
    private final ScheduledFutureStoreComponent scheduledFutureStoreComponent;

    public ScrumHelpBot(ScrumHelpBotService scrumHelpBotService,
                        TelegramBotConfig telegramBotConfig,
                        ThreadPoolTaskScheduler threadPoolTaskScheduler,
                        ScheduledFutureStoreComponent scheduledFutureStoreComponent) {
        this.scrumHelpBotService = scrumHelpBotService;
        this.telegramBotConfig = telegramBotConfig;
        this.threadPoolTaskScheduler = threadPoolTaskScheduler;
        this.scheduledFutureStoreComponent = scheduledFutureStoreComponent;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasCallbackQuery()) {
                if (update.getCallbackQuery().getData().contains("/newFacilitator")) {
                    Long chatId = update.getCallbackQuery().getMessage().getChatId();
                    execute(scrumHelpBotService.removeMarkupFromPreviousMessage(chatId, update.getCallbackQuery().getMessage().getMessageId()));
                    execute(scrumHelpBotService.sendSetFacilitatorSelectedMessage(chatId, update));
                }
            } else {
                if (!update.hasMessage()) return;

                String messageText = update.getMessage().getText();
                Long chatId = update.getMessage().getChatId();

                if ("/on".equals(messageText)) {
                    execute(scrumHelpBotService.sendRegisterUserMessage(chatId, update.getMessage().getFrom()));
                } else if ("/off".equals(messageText) || update.getMessage().getLeftChatMember() != null) {
                    execute(scrumHelpBotService.sendRemoveUserMessage(chatId, update.getMessage().getFrom()));
                } else if ("/setFacilitator".equals(messageText)) {
                    execute(scrumHelpBotService.sendSelectFacilitatorMessage(chatId));
                } else if ("/luckyFacilitator".equals(messageText)) {
                    execute(scrumHelpBotService.sendSetFacilitatorSelectedMessage(chatId));
                } else if ("/enableDailyReminder".equals(messageText)) {
                    execute(sendDailyReminderEnableMessage(chatId));
                } else if ("/disableDailyReminder".equals(messageText)) {
                    execute(sendDailyReminderDisableMessage(chatId));
                } else if ("/getUserList".equals(messageText)) {
                    execute(scrumHelpBotService.sendUserListMessage(chatId));
                } else if ("/help".equals(messageText)) {
                    execute(scrumHelpBotService.sendHelpMessage(chatId));
                }
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private SendMessage sendDailyReminderEnableMessage(Long chatId) {
        if (!scheduledFutureStoreComponent.checkExist(chatId)) {
            scheduledFutureStoreComponent.add(chatId, threadPoolTaskScheduler.schedule(() -> {
                try {
                    execute(scrumHelpBotService.sendRemindDailyMessage(chatId));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }, new CronTrigger("0 29 10 ? * MON-FRI", TimeZone.getTimeZone("Europe/Moscow"))));

            log.info("ScheduleDailyRemindManager schedule new RemindDailyTask: 0 29 10 ? * MON-FRI");
            return scrumHelpBotService.sendDailyReminderMessage(chatId, TurnedOn);
        }
        return scrumHelpBotService.sendDailyReminderMessage(chatId, AlreadySet);
    }

    private SendMessage sendDailyReminderDisableMessage(Long chatId) {
        if (scheduledFutureStoreComponent.checkExist(chatId)) {
            scheduledFutureStoreComponent.remove(chatId);
            return scrumHelpBotService.sendDailyReminderMessage(chatId, TurnedOff);
        } else {
            return scrumHelpBotService.sendDailyReminderMessage(chatId, NotSet);
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