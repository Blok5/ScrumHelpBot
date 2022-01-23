package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.model.ChatMemberId;
import com.example.scrumhelp.scrum.repository.ChatMemberRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMemberServiceImpl implements ChatMemberService {
    private final ChatMemberRepository chatMemberRepository;

    public ChatMemberServiceImpl(ChatMemberRepository chatMemberRepository) {
        this.chatMemberRepository = chatMemberRepository;
    }

    public Optional<ChatMember> findChatMemberForChat(Long chatId, Long memberId) {
        return chatMemberRepository.findChatMemberByChat_IdAndMember_Id(chatId, memberId);
    }

    public List<ChatMember> findChatMembersExceptFacilitator(Long chatId) {
        return chatMemberRepository.findChatMembersByChat_IdAndIsFacilitator(chatId, false);
    }

    public Optional<ChatMember> findFacilitatorForChat(Long chatId, Boolean isFacilitator) {
        return chatMemberRepository.findChatMemberByChat_IdAndIsFacilitator(chatId, isFacilitator);
    }

    public Optional<List<Chat>> findChats() {
        return chatMemberRepository.findDistinctChats();
    }

    public List<ChatMember> findChatMembers(Long chatId) {
        return chatMemberRepository.findAllByChat_Id(chatId);
    }

    //TODO: unit test changeAndGetNewFacilitatorForChat
    @Transactional
    public Optional<ChatMember> changeAndGetNewFacilitatorForChat(Long chatId, Long newFacilitatorId) {
        findFacilitatorForChat(chatId, true)
                .ifPresent(chatMember -> {
                    chatMember.setIsFacilitator(false);
                    chatMemberRepository.save(chatMember);
                });

        Optional<ChatMember> newFacilitatorOptional = findChatMemberForChat(chatId, newFacilitatorId);
        if (newFacilitatorOptional.isPresent()) {
            ChatMember newFacilitator = newFacilitatorOptional.get();
            newFacilitator.setIsFacilitator(true);
            chatMemberRepository.save(newFacilitator);
        }

        return newFacilitatorOptional;
    }

    //TODO: unit test
    @Override
    public void removeChatMember(ChatMemberId chatMemberId) {
        chatMemberRepository.deleteById(chatMemberId);
    }
}
