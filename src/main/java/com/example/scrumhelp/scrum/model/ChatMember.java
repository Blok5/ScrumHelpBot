package com.example.scrumhelp.scrum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "ChatMember")
@Table(name = "chat_member")
@NoArgsConstructor
@Setter
@Getter
public class ChatMember {

    @EmbeddedId
    private ChatMemberId chatMemberId;

    @Column(name = "is_facilitator")
    private Boolean isFacilitator;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(
            name = "member_id",
            foreignKey = @ForeignKey(
                    name = "fk_chatmember_member_id"
            )
    )
    private Member member;

    @ManyToOne
    @MapsId("chatId")
    @JoinColumn(
            name = "chat_id",
            foreignKey = @ForeignKey(
                    name = "fk_chatmember_chat_id"
            )
    )
    private Chat chat;

    public ChatMember(Member member, Chat chat, Boolean isFacilitator) {
        this.isFacilitator = isFacilitator;
        this.member = member;
        this.chat = chat;
    }
}
