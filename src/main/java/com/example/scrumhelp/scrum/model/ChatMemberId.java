package com.example.scrumhelp.scrum.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class ChatMemberId implements Serializable {

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "chat_id")
    private Long chatId;

    public ChatMemberId(Long memberId, Long chatId) {
        this.memberId = memberId;
        this.chatId = chatId;
    }

}
