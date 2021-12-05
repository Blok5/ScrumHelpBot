package com.example.scrumhelp.scrum.repository;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.model.ChatMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatMemberRepository extends JpaRepository<ChatMember, ChatMemberId> {
    Optional<ChatMember> findChatMemberByChat_IdAndIsFacilitator(Long chatId, Boolean isFacilitator);
    Optional<List<ChatMember>> findAllByChat_Id(Long chatId);
    Optional<ChatMember> findChatMemberByChat_IdAndMember_Id(Long chatId, Long memberId);
    @Query("SELECT DISTINCT chat FROM ChatMember")
    Optional<List<Chat>> findDistinctChats();
}
