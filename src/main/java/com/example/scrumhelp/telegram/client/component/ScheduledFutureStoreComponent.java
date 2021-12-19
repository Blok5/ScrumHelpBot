package com.example.scrumhelp.telegram.client.component;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Component
public class ScheduledFutureStoreComponent {
    private final Map<Long, ScheduledFuture<?>> scheduledFutureMap;

    public ScheduledFutureStoreComponent(Map<Long, ScheduledFuture<?>> scheduledFutureMap) {
        this.scheduledFutureMap = scheduledFutureMap;
    }

    public void add(Long chatId, ScheduledFuture<?> scheduledFuture) {
        scheduledFutureMap.put(chatId, scheduledFuture);
    }

    public void remove(Long chatId) {
        if (scheduledFutureMap.containsKey(chatId)) {
            scheduledFutureMap.get(chatId).cancel(true);
            scheduledFutureMap.remove(chatId);
        }
    }

    public Boolean checkExist(Long chatId) {
        return scheduledFutureMap.containsKey(chatId);
    }

}
