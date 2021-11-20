package com.example.scrumhelp.scrum.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Chat")
@Table(name = "chat")
public class Chat {
    @Id
    @Column(name = "id", updatable = false)
    private Long id;

    //TODO: refactor FetchType.EAGER
    @OneToMany(
            mappedBy = "chat",
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            orphanRemoval = true
    )
    private List<ChatMember> chatMembers = new ArrayList<>();

    public Chat(Long id) {
        this.id = id;
    }

    public Chat() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addChatMember(ChatMember chatMember) {
        if (!this.chatMembers.contains(chatMember)) {
            this.chatMembers.add(chatMember);
            chatMember.setChat(this);
        }
    }

    public void removeChatMember(ChatMember chatMember) {
        if (this.chatMembers.contains(chatMember)) {
            this.chatMembers.remove(chatMember);
            chatMember.setChat(null);
        }
    }

    public List<ChatMember> getChatMembers() {
        return this.chatMembers;
    }
}
