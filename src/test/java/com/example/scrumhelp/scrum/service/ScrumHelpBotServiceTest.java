package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.ScrumHelpApplication;
import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.repository.ChatMemberRepository;
import com.example.scrumhelp.scrum.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ScrumHelpBotServiceTest {

    @MockBean
    private ScrumHelpBotService scrumHelpBotService;
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatMemberRepository chatMemberRepository;

//    @Test
//    void registrFirstUserInEmptyGroup() {
//        User user = new User(3L,
//                "firsName",
//                false,
//                "lastName",
//                "userName",
//                "ru",
//                true,
//                true,
//                true);

//        scrumHelpBotService.registrUser(2L, user);
//        assertThat(chatRepository.getById(2L)).isNotNull();
//        assertThat(chatMemberRepository.getById(3L)).isNotNull();
//        Optional<ChatMember> chatMember= chatMemberRepository.findById(3L);
//        assertThat(chatMember.isPresent()).isTrue();
}
