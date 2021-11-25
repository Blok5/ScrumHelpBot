package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.enums.DailyReminderState;
import com.example.scrumhelp.scrum.enums.Emoji;
import com.example.scrumhelp.scrum.model.Chat;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.model.Member;
import com.example.scrumhelp.scrum.repository.ChatMemberRepository;
import com.example.scrumhelp.scrum.repository.MemberRepository;
import com.example.scrumhelp.scrum.repository.ChatRepository;
import eye2web.modelmapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ScrumHelpBotService(MemberRepository memberRepository,
                               ChatRepository chatRepository,
                               ChatMemberRepository chatMemberRepository,
                               ModelMapper modelMapper)
    {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.chatMemberRepository = chatMemberRepository;
        this.modelMapper = modelMapper;
    }

    public SendMessage sendRegisterUserMessage(Long chatId, User fromUser) {
        StringBuilder responseText = new StringBuilder();
        Member member = memberRepository.findById(fromUser.getId()).orElse(modelMapper.map(fromUser, Member.class));

        chatMemberRepository.findChatMemberByChat_IdAndAndMember_Id(chatId, member.getId()).ifPresentOrElse(
                chatMember -> {
                    responseText.append("Пользователь ").append(member.getNickNamne()).append(" уже зарегистрирован!").append(OkHad);
                    log.warn(member.getNickNamne() + " already exist!");
                }, () -> {
                    Chat chat = chatRepository.findById(chatId).orElse(new Chat(chatId));
                    memberRepository.save(member);
                    chat.addMember(member);
                    chatRepository.save(chat);

                    responseText.append("Пользователь ").append(member.getNickNamne())
                            .append(" успешно зарегистрирован!").append(Emoji.PartyingFace);
                    log.info(member.getNickNamne() + " user registered for chat: " + chatId + " successfully!");
                });

        return new SendMessage(chatId.toString(), responseText.toString());
    }

    public SendMessage sendDailyReminderMessage(Long chatId, DailyReminderState dailyReminderState) {
        SendMessage sendMessage = new SendMessage();
        switch (dailyReminderState) {
            case TurnedOff: {
                sendMessage.setText("Напоминание о дейли выключено!" + CrossMark);
                log.info("Daily reminder for chat " + chatId + " disabled!");
                break;
            }
            case NotSet: {
                sendMessage.setText("Напоминание о дейли еще не установлено!" + RedExclamation +
                        "\nДля включения напоминания /enableDailyReminder");
                break;
            }
            case TurnedOn: {
                sendMessage.setText("Напоминание о дейли включено!" + CheckMarkButton + "\n" +
                        "Для выключения напоминания:\n/disableDailyReminder");
                break;
            }
            case AlreadySet: {
                sendMessage.setText("Напоминание о дейли уже установлено!" + RedExclamation);
                break;
            }
        }
        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    public SendMessage sendRemindDailyMessage(Long chatId) {
        Optional<ChatMember> chatMember = chatMemberRepository.findChatMemberByChat_IdAndIsFacilitator(chatId, true);
        String name = chatMember.isPresent() ? chatMember.get().getMember().getNickNamne() : "Не назначен!";

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

    public SendMessage sendSelectFacilitatorMessage(Long chatId) {
        Optional<List<ChatMember>> chatMembers = chatMemberRepository.findAllByChat_Id(chatId);

        List<List<InlineKeyboardButton>> keyboardButtonsRowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        if (chatMembers.isPresent()) {
            for (ChatMember chatMember : chatMembers.get()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(chatMember.getMember().getNickNamne());
                button.setCallbackData("/newFacilitator " + chatMember.getMember().getId());

                keyboardButtonsRow.add(button);
                if (keyboardButtonsRow.size() == 2) {
                    keyboardButtonsRowList.add(keyboardButtonsRow);
                    keyboardButtonsRow = new ArrayList<>();
                }
            }

            if (!keyboardButtonsRow.isEmpty()) {
                keyboardButtonsRowList.add(keyboardButtonsRow);
            }

            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            keyboardMarkup.setKeyboard(keyboardButtonsRowList);

            SendMessage sendMessage = new SendMessage(chatId.toString(), "Выбери следующего фасилитатора:" + PoliceOfficer);
            sendMessage.setReplyMarkup(keyboardMarkup);
            return sendMessage;
        }

        return new SendMessage(chatId.toString(), "Список зарегистрированных пользователей пуст");
    }

    @Transactional
    public SendMessage sendNewFacilitatorSelectedMessage(Long chatId, String callbackData) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        Long newFacilitatorId = Long.parseLong(callbackData.split(" ")[1]);

        chatMemberRepository.findChatMemberByChat_IdAndIsFacilitator(chatId, true)
                .ifPresent(chatMember -> {
                    chatMember.setIsFacilitator(false);
                    chatMemberRepository.save(chatMember);
                });

        chatMemberRepository.findChatMemberByChat_IdAndAndMember_Id(chatId, newFacilitatorId).ifPresentOrElse(
                chatMember -> {
                    chatMember.setIsFacilitator(true);
                    sendMessage.setText(String.format("Следующий фасилитатор: %s", chatMember.getMember().getNickNamne()));
                    log.info(String.format("For chat %s selected new facilitator %s", chatId, chatMember.getMember().getNickNamne()));
                }, () -> sendMessage.setText(String.format("Участник с id: %s не найден", newFacilitatorId)));

        return sendMessage;
    }

    public SendMessage sendUserListMessage(Long chatId) {
        StringBuilder responseText = new StringBuilder();
        responseText.append("Зарегистрированные пользователи:\n");

        chatMemberRepository.findAllByChat_Id(chatId).ifPresentOrElse(
                chatMembers -> chatMembers.forEach(chatMember ->
                                responseText.append(chatMember.getMember().getNickNamne()).append("\n")),
                () -> responseText.append("Список пуст")
        );

        return new SendMessage(chatId.toString(), responseText.toString());
    }

    public SendMessage sendHelpMessage(Long chatId) {
        return new SendMessage(chatId.toString(),
                "Список возможных команд:\n" +
                "/register - регистрация пользователя\n" +
                "/getUserList - список участников\n" +
                "/setFacilitator - выбор фасилитатора\n" +
                "/enableDailyReminder - включить напоминание о дейли\n" +
                "/disableDailyReminder- выключить напоминание о дейли"
        );
    }

    public EditMessageReplyMarkup removeMarkupFromPreviousMessage(Long chatId, Integer messageId) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId.toString());
        editMessageReplyMarkup.setMessageId(messageId);
        return editMessageReplyMarkup;
    }

    public Optional<List<Chat>> findChats() {
        return chatMemberRepository.findDistinctChats();
    }
}
