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
    private Boolean isFacilitator;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne()
    @JoinColumn(
            name = "chat_id",
            foreignKey = @ForeignKey(
                    name = "fk_chat_member_chat"
            )
    )
    private Chat chat;

    public ChatMember(Long id,
                      String userName,
                      Boolean isFacilitator,
                      String firstName,
                      String lastName)
    {
        this.id = id;
        this.userName = userName;
        this.isFacilitator = isFacilitator;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public Boolean getFacilitator() {
        return isFacilitator;
    }

    public void setFacilitator(Boolean facilitator) {
        isFacilitator = facilitator;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getChatName() {
        if (getUserName() == null) {
            if (!getFullName().equals("")) {
                return getFullName();
            } else {
                return getId().toString();
            }
        }
        return getUserName();
    }

    private String getFullName() {
        String result = "";

        if (getFirstName() != null) {
            result += getFirstName();
        }
        if (getLastName() != null) {
            result += getLastName();
        }

        return result;
    }

    @Override
    public String toString() {
        return "ChatMember{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", isFacilitator=" + isFacilitator +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
