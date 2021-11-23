package com.example.scrumhelp.scrum.component;

import com.example.scrumhelp.scrum.service.ScrumHelpBot;
import com.example.scrumhelp.scrum.service.ScrumHelpBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@EnableScheduling
@Slf4j
public class ScheduledSelectFacilitatorComponent {
    private final ScrumHelpBot scrumHelpBot;
    private final ScrumHelpBotService scrumHelpBotService;
    private final TaskExecutor taskExecutor;

    @Autowired
    public ScheduledSelectFacilitatorComponent(ScrumHelpBot scrumHelpBot,
                                               ScrumHelpBotService scrumHelpBotService,
                                               TaskExecutor taskExecutor) {
        this.scrumHelpBot = scrumHelpBot;
        this.scrumHelpBotService = scrumHelpBotService;
        this.taskExecutor = taskExecutor;
    }

    @Scheduled(cron = "0 45 10 ? * MON-FRI", zone = "Europe/Moscow")
    private void schedule() {
        scrumHelpBotService.getAllChats().forEach(chat ->
                taskExecutor.execute(() -> {
                    try {
                        log.info("Running SelectFacilitatorTask for chat " + chat.getId());
                        scrumHelpBot.execute(scrumHelpBotService.sendSelectFacilitatorMessage(chat.getId()));
                    } catch (TelegramApiException e) {
                        log.error(e.getMessage());
                    }
                    log.info("Finished SelectFacilitatorTask for chat " + chat.getId());
                }));
    }
}
