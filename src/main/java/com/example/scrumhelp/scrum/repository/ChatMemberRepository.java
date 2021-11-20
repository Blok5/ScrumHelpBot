package com.example.scrumhelp.scrum.repository;

import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMemberRepository extends JpaRepository<ChatMember, Long> {

    @Query("SELECT c FROM ChatMember c WHERE c.isFacilitator = true and c.chat = ?1")
    Optional<ChatMember> findFacilitatorByChat(Chat chat);

    @Query(value = "SELECT * FROM chatmember WHERE chat_id = :chatId", nativeQuery = true)
    List<ChatMember> findAllByChat(@Param("chatId") Long chatId);
}
