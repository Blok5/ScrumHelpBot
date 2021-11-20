package com.example.scrumhelp.scrum.component;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.service.ScrumHelpBot;
import com.example.scrumhelp.scrum.service.ScrumHelpBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
@EnableScheduling
public class ScheduledSelectFacilitatorComponent {
    private final static Logger log = LoggerFactory.getLogger(ScheduledSelectFacilitatorComponent.class);
    private final ScrumHelpBot scrumHelpBot;
    private final ScrumHelpBotService scrumHelpBotService;
    private final TaskExecutor taskExecutor;

    @Autowired
    public ScheduledSelectFacilitatorComponent(ScrumHelpBot scrumHelpBot,
                                               ScrumHelpBotService scrumHelpBotService,
                                               TaskExecutor taskExecutor)
    {
        this.scrumHelpBot = scrumHelpBot;
        this.scrumHelpBotService = scrumHelpBotService;
        this.taskExecutor = taskExecutor;
    }

    //0 29 10 ? * MON-FRI
    @Scheduled(cron = "0 45 10 ? * MON-FRI")
    private void schedule() {
        List<Chat> chats = scrumHelpBotService.getAllChats();
        if (chats.isEmpty()) {
            log.info("Can't run SelectFacilitatorTask due to chats.isEmpty!");
        } else {
            chats.forEach(chat -> taskExecutor.execute(new SelectFacilitatorTask(chat.getId())));
        }
    }

    private class SelectFacilitatorTask implements Runnable {
        private final Long chatId;

        public SelectFacilitatorTask(Long chatId) {
            this.chatId = chatId;
        }

        @Override
        public void run() {
            try {
                log.info("Running SelectFacilitatorTask for chat " + chatId);
                scrumHelpBot.execute(scrumHelpBotService.sendSelectFacilitatorMessage(chatId));
            } catch (TelegramApiException e) {
                log.error(e.getMessage());
            }
            log.info("Finished SelectFacilitatorTask for chat " + chatId);
        }
    }

}
