package com.example.scrumhelp.scrum.model;

import javax.persistence.*;

@Entity(name = "ChatMember")
@Table(name = "chatmember")
public class ChatMember {
    @Id
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "isfacilitator")
    private boolean isFacilitator;

    @ManyToOne()
    @JoinColumn(
            name = "chat_id",
            foreignKey = @ForeignKey(
                    name = "fk_chat_member_chat"
            )
    )
    private Chat chat;

    public ChatMember(Long id, String userName, boolean isFacilitator) {
        this.id = id;
        this.userName = userName;
        this.isFacilitator = isFacilitator;
    }

    public ChatMember() {
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isFacilitator() {
        return isFacilitator;
    }

    public void setFacilitator(boolean facilitator) {
        isFacilitator = facilitator;
    }

//    @Override
//    public String toString() {
//        return "ChatMember{" +
//                "id=" + id +
//                ", userName='" + userName + '\'' +
//                ", isFacilitator=" + isFacilitator +
//                '}';
//    }
}
