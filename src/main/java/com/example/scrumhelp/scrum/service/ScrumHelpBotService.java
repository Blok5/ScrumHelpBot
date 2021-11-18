package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.enums.Emoji;
import com.example.scrumhelp.scrum.enums.RegisterResult;
import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.repository.ChatMemberRepository;
import com.example.scrumhelp.scrum.repository.ChatRepository;
import eye2web.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

import static com.example.scrumhelp.scrum.enums.RegisterResult.Exist;
import static com.example.scrumhelp.scrum.enums.RegisterResult.Success;

@Service
public class ScrumHelpBotService {

    private final static Logger log = LoggerFactory.getLogger(ScrumHelpBotService.class);
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ScrumHelpBotService(ChatMemberRepository chatMemberRepository,
                               ChatRepository chatRepository,
                               ModelMapper modelMapper
    ) {
        this.chatMemberRepository = chatMemberRepository;
        this.chatRepository = chatRepository;
        this.modelMapper = modelMapper;
    }

    public String registerUser(Long chatId, User fromUser) {
        Chat chat = chatRepository.findById(chatId).orElse(new Chat(chatId));

        Optional<ChatMember> chatMemberOptional = chatMemberRepository.findById(fromUser.getId());

        if (chatMemberOptional.isPresent()) {
            return prepareResponseMessage(Exist, chatMemberOptional.get(), chat).toString();
        } else {
            ChatMember chatMember = modelMapper.map(fromUser, ChatMember.class);
            chat.addChatMember(chatMember);
            chatRepository.save(chat);

            return prepareResponseMessage(Success, chatMember, chat).toString();
        }
    }

    private StringBuilder prepareResponseMessage(RegisterResult registerResult,
                                                 ChatMember chatMember,
                                                 Chat chat) {
        StringBuilder responseText = new StringBuilder();
        responseText.append("Пользователь ").append(chatMember.getUserName());

        switch (registerResult) {
            case Success: {
                log.info(chatMember.getUserName() + " added");
                responseText.append(" успешно зарегистрирован!")
                        .append(Emoji.PartyingFace.getText()).append("\n\n")
                        .append("Зарегистрированные пользователи:\n");

                chat.getChatMembers().forEach(chatMember1 ->
                        responseText.append(chatMember1.getUserName()).append("\n"));
                break;
            }
            case Exist: {
                log.warn(chatMember.getUserName() + " already exist!");
                responseText.append(" уже зарегистрирован!").append(Emoji.OkHad.getText());
                break;
            }
        }
        return responseText;
    }

//    //TODO: CRON TRIGGER
//    public void scheduleRemind(ScrumHelpBot scrumHelpBot) {
//        threadPoolTaskScheduler.schedule(new RunnableTask(scrumHelpBot),
//                new CronTrigger("*/10 * * * * *"));
//    }
////
//    class RunnableTask implements Runnable{
//        ScrumHelpBot scrumHelpBot;
//        public RunnableTask(ScrumHelpBot scrumHelpBot){
//            this.scrumHelpBot = scrumHelpBot;
//        }
//
//        @Override
//        public void run() {
//            StringBuilder remindText = new StringBuilder();
//            remindText.append(Emoji.YawningFace.getText())
//                    .append("В 10:30 начнется Daily!\n\n")
//                    .append(Emoji.Memo.getText()).append("Вспомни:\n")
//                    .append("Что сделано вчера?\n")
//                    .append("Что будет сделано сегодня?\n")
//                    .append("С какими проблемами столкнулся?\n\n");
//
//            SendMessage sendMessage;
//            for (var entry : getChats().entrySet()) {
//                sendMessage = new SendMessage(entry.getKey().toString(), remindText.toString());
//                try {
//                    scrumHelpBot.execute(sendMessage);
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
}
