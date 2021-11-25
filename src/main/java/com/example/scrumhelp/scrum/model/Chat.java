package com.example.scrumhelp.scrum.model;

import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity(name = "Chat")
@Table(name = "chat")
@NoArgsConstructor
public class Chat {
    @Id
    @Column(name = "id", updatable = false)
    private Long id;

    @OneToMany(
            mappedBy = "chat",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ChatMember> chatMembers = new ArrayList<>();

    public Chat(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void addMember(Member member) {
        ChatMember chatMember = new ChatMember(member, this, false);
        chatMember.setChatMemberId(new ChatMemberId(member.getId(), id));
        chatMembers.add(chatMember);
        member.getChatMembers().add(chatMember);
    }

    public void removeMember(Member member) {
        for (Iterator<ChatMember> iterator = chatMembers.iterator();
             iterator.hasNext(); ) {
            ChatMember chatMember = iterator.next();

            if (chatMember.getChat().equals(this) &&
                    chatMember.getMember().equals(member)) {
                iterator.remove();
                chatMember.getMember().getChatMembers().remove(chatMember);
                chatMember.setChat(null);
                chatMember.setMember(null);
            }
        }
    }
}
