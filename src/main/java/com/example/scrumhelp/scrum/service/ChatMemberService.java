package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface ChatMemberService {
    Optional<ChatMember> findChatMemberForChat(Long chatId, Long memberId);

    List<ChatMember> findChatMembersExceptFacilitator(Long chatId);

    Optional<ChatMember> findFacilitatorForChat(Long chatId, Boolean isFacilitator);

    Optional<List<Chat>> findChats();

    List<ChatMember> findChatMembers(Long chatId);

    @Transactional
    Optional<ChatMember> changeAndGetNewFacilitatorForChat(Long chatId, Long newFacilitatorId);
}
