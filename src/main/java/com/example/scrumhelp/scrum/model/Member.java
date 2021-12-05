package com.example.scrumhelp.scrum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Member")
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @Column(
            name = "id",
            updatable = false
    )
    private Long id;

    @Column(name = "username")
    private String userName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "member",
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ChatMember> chatMembers = new ArrayList<>();

    public String getNickName() {

        if (getFullName().equals("")) {
            if (getUserName() != null) {
                return getUserName();
            } else {
                return getId().toString();
            }
        }
        return getFullName();
    }

    private String getFullName() {
        String result = "";

        if (getFirstName() != null) {
            result += getFirstName() + " ";
        }
        if (getLastName() != null) {
            result += getLastName();
        }

        return result.trim();
    }

    @Override
    public String toString() {
        return "ChatMember{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
