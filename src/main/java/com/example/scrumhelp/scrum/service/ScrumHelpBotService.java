package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.enums.DailyReminderState;
import com.example.scrumhelp.scrum.enums.Emoji;
import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.repository.ChatMemberRepository;
import com.example.scrumhelp.scrum.repository.ChatRepository;
import eye2web.modelmapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.scrumhelp.scrum.enums.Emoji.*;

@Service
@Slf4j
public class ScrumHelpBotService {
    private final ChatMemberRepository chatMemberRepository;
    private final ChatRepository chatRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ScrumHelpBotService(ChatMemberRepository chatMemberRepository,
                               ChatRepository chatRepository,
                               ModelMapper modelMapper) {
        this.chatMemberRepository = chatMemberRepository;
        this.chatRepository = chatRepository;
        this.modelMapper = modelMapper;
    }

    public SendMessage sendDailyReminderMessage(Long chatId,
                                                       DailyReminderState dailyReminderState,
                                                       Trigger trigger) {
        SendMessage sendMessage = new SendMessage();

        switch (dailyReminderState) {
            case TurnedOff: {
                sendMessage.setText("Напоминание о дейли выключено!" + CrossMark);
                log.info("Daily reminder for chat " + chatId + " disabled!");
                break;
            }
            case NotSet:{
                sendMessage.setText("Напоминание о дейли еще не установлено!" + RedExclamation +
                        "\nДля включения напоминания /enableDailyReminder");
                break;
            }
            case TurnedOn: {
                sendMessage.setText("Напоминание о дейли включено!" + CheckMarkButton + "\n"
                        + trigger + "\nДля выключения напоминания:\n/disableDailyReminder");
                break;
            }
            case AlreadySet: {
                sendMessage.setText("Напоминание о дейли уже установлено!" + RedExclamation +
                        "\nВремя напоминания: " + trigger);
                break;
            }
        }

        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    public SendMessage sendRemindDailyMessage(Long chatId) {
        Optional<ChatMember> facilitator =
                chatMemberRepository.findFacilitatorByChat(chatRepository.findById(chatId).orElseThrow());

        String name = facilitator.isPresent() ? facilitator.get().getUserName() : "Не назначен!";

        String remindText = Emoji.YawningFace.getText() +
                "В 10:30 начнется Daily!\n\n" +
                Emoji.Memo.getText() + "Вспомни:\n" +
                "Что сделано вчера?\n" +
                "Что будет сделано сегодня?\n" +
                "С какими проблемами столкнулся?\n\n" +
                "Сегодня фасилитатор:\n" +
                name;

        return new SendMessage(chatId.toString(), remindText);
    }

    public SendMessage sendRegisterUserMessage(Long chatId, User fromUser) {
        Chat chat = chatRepository.findById(chatId).orElse(new Chat(chatId));
        Optional<ChatMember> chatMemberOptional = chatMemberRepository.findById(fromUser.getId());

        StringBuilder responseText = new StringBuilder();

        if (chatMemberOptional.isPresent()) {
            String userName = chatMemberOptional.get().getUserName();
            responseText.append("Пользователь ").append(userName).append(" уже зарегистрирован!").append(OkHad);
            log.warn(userName + " already exist!");
        } else {
            ChatMember chatMember = modelMapper.map(fromUser, ChatMember.class);
            chat.addChatMember(chatMember);
            chatRepository.save(chat);

            responseText.append("Пользователь ").append(chatMember.getUserName()).append(" успешно зарегистрирован!")
                    .append(Emoji.PartyingFace)
                    .append("\n\nЗарегистрированные пользователи:\n");
            chat.getChatMembers().forEach(cm -> responseText.append(cm.getUserName()).append("\n"));

            log.info(chatMember.getUserName() + " user registered successfully!");
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(responseText.toString());
        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    public SendMessage sendSelectFacilitatorMessage(Long chatId) {
        List<ChatMember> chatMembers = chatMemberRepository.findAllByChat(chatId);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        if (chatMembers.size() > 0) {
            for (ChatMember chatMember : chatMembers) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(chatMember.getUserName());
                button.setCallbackData("/newFacilitator " + chatMember.getId());
                keyboardButtonsRow.add(button);
            }
        }

        List<List<InlineKeyboardButton>> keyboardButtonsRowList = new ArrayList<>();
        keyboardButtonsRowList.add(keyboardButtonsRow);
        keyboardMarkup.setKeyboard(keyboardButtonsRowList);

        SendMessage sendMessage =
                new SendMessage(chatId.toString(), "Выбери следующего фасилитатора:" + PoliceOfficer);
        sendMessage.setReplyMarkup(keyboardMarkup);
        return sendMessage;
    }

    public SendMessage sendHelpMessage(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText("Список возможных команд:\n" +
                "/register\n" +
                "/setFacilitator\n" +
                "/enableDailyReminder\n" +
                "/disableDailyReminder");
        return sendMessage;
    }

    @Transactional
    public SendMessage sendNewFacilitatorSelectedMessage(Long chatId, String callbackData) {
        Optional<ChatMember> currentFacilitatorOptional =
                chatMemberRepository.findFacilitatorByChat(chatRepository.findById(chatId).orElseThrow());

        if (currentFacilitatorOptional.isPresent()) {
            ChatMember currentFacilitator = currentFacilitatorOptional.get();
            currentFacilitator.setFacilitator(false);
            chatMemberRepository.save(currentFacilitator);
        }

        Long newFacilitatorId = Long.parseLong(callbackData.split(" ")[1]);
        Optional<ChatMember> newFacilitator = chatMemberRepository.findById(newFacilitatorId);

        SendMessage sendMessage = new SendMessage();
        if (newFacilitator.isPresent()) {
            ChatMember chatMember = newFacilitator.get();
            chatMember.setFacilitator(true);
            sendMessage.setText(String.format("Следующий фасилитатор: %s", chatMember.getUserName()));

            log.info(String.format("For chat %s selected new facilitator %s", chatId, chatMember.getUserName()));
        } else {
            sendMessage.setText(String.format("Участник с id: %s не найден", newFacilitatorId));

            log.warn(String.format("Chat member with id %s does not exist", newFacilitatorId));
        }

        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    public EditMessageReplyMarkup removeMarkupFromPreviousMessage(Long chatId, Integer messageId) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId.toString());
        editMessageReplyMarkup.setMessageId(messageId);
        return editMessageReplyMarkup;
    }

    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }
}
