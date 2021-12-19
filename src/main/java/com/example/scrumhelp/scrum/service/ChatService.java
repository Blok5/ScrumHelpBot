package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.Member;

public interface ChatService {
    void addMember(Member member, Long chatId);

    Chat findOrCreate(Long chatId);
}
