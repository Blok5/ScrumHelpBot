package com.example.scrumhelp.scrum.component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

public final class ScheduledFutureStoreComponent {
    private static ScheduledFutureStoreComponent instance;
    private static Map<Long, ScheduledFuture<?>> scheduledFutureMap;

    public static ScheduledFutureStoreComponent getInstance() {
        if (instance == null) {
            scheduledFutureMap = new HashMap<>();
            instance = new ScheduledFutureStoreComponent();
        }
        return instance;
    }

    public void addScheduledFutureForChatId(Long chatId, ScheduledFuture<?> scheduledFuture) {
        scheduledFutureMap.put(chatId, scheduledFuture);
    }

    public void removeScheduledFutureByChatId(Long chatId) {
        if (scheduledFutureMap.containsKey(chatId)) {
            scheduledFutureMap.get(chatId).cancel(true);
            scheduledFutureMap.remove(chatId);
        }
    }

    public Boolean checkExistScheduledFutureForChatId(Long chatId) {
        return scheduledFutureMap.containsKey(chatId);
    }

}
