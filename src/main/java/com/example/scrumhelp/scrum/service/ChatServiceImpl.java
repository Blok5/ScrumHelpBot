package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.Member;
import com.example.scrumhelp.scrum.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;

    @Autowired
    public ChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void addMember(Member member, Long chatId) {
        Chat chat = findOrCreate(chatId);
        chat.addMember(member);
        chatRepository.save(chat);
    }

    public Chat findOrCreate(Long chatId) {
        Optional<Chat> chatOptional = chatRepository.findById(chatId);

        if (chatOptional.isEmpty()) {
            Chat chat = new Chat(chatId);
            chatRepository.save(chat);
            return chat;
        }

        return chatOptional.get();
    }

}
