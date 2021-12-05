package com.example.scrumhelp.scrum.service;

import com.example.scrumhelp.scrum.enums.DailyReminderState;
import com.example.scrumhelp.scrum.enums.Emoji;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.model.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.scrumhelp.scrum.enums.Emoji.*;
import static java.lang.String.format;

@Service
@Slf4j
public class ScrumHelpBotService {
    private final ChatMemberService chatMemberService;
    private final MemberService memberService;
    private final ChatService chatService;

    @Autowired
    public ScrumHelpBotService(ChatService chatService,
                               ChatMemberService chatMemberService,
                               MemberService memberService)
    {
        this.chatService = chatService;
        this.chatMemberService = chatMemberService;
        this.memberService = memberService;
    }

    public SendMessage sendRegisterUserMessage(Long chatId, User fromUser) {
        SendMessage sendMessage = new SendMessage();
        Member member = memberService.findOrCreate(fromUser);

        chatMemberService.findChatMemberForChat(chatId, member.getId()).ifPresentOrElse(
                chatMember -> {
                    sendMessage.setText(format("Пользователь %s уже зарегистрирован!%s" , member.getNickName(), OkHad));
                    log.warn(member.getNickName() + " already exist!");
                }, () -> {
                    chatService.addMember(member, chatId);
                    sendMessage.setText(format("Пользователь %s успешно зарегистрирован!%s", member.getNickName(), PartyingFace));
                    log.info(member.getNickName() + " user registered for chat: " + chatId + " successfully!");
                });

        sendMessage.setChatId(chatId.toString());
        return sendMessage;
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
        Optional<ChatMember> chatMember = chatMemberService.findFacilitatorForChat(chatId, true);
        String name = chatMember.isPresent() ? chatMember.get().getMember().getNickName() : "Не назначен!";

        String remindText = YawningFace.getText() +
                "В 10:30 начнется Daily!\n\n" +
                Memo.getText() + "Вспомни:\n" +
                "Что сделано вчера?\n" +
                "Что будет сделано сегодня?\n" +
                "С какими проблемами столкнулся?\n\n" +
                "Сегодня фасилитатор:\n" +
                name;

        return new SendMessage(chatId.toString(), remindText);
    }

    public SendMessage sendSelectFacilitatorMessage(Long chatId) {
        Optional<List<ChatMember>> chatMembers = chatMemberService.findChatMembers(chatId);
        List<List<InlineKeyboardButton>> keyboardButtonsRowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        if (chatMembers.isPresent()) {
            for (ChatMember chatMember : chatMembers.get()) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(chatMember.getMember().getNickName());
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

    public SendMessage sendNewFacilitatorSelectedMessage(Long chatId, String callbackData) {
        SendMessage sendMessage = new SendMessage();
        Long newFacilitatorId = Long.parseLong(callbackData.split(" ")[1]);

        chatMemberService.changeFacilitatorForChat(chatId, newFacilitatorId).ifPresentOrElse(
                chatMember -> {
                    sendMessage.setText(format("Следующий фасилитатор: %s", chatMember.getMember().getNickName()));
                    log.info(format("For chat %s selected new facilitator %s", chatId, chatMember.getMember().getNickName()));
                }, () -> sendMessage.setText(format("Участник с id: %s не найден", newFacilitatorId)));

        sendMessage.setChatId(chatId.toString());
        return sendMessage;
    }

    public SendMessage sendUserListMessage(Long chatId) {
        StringBuilder responseText = new StringBuilder();
        responseText.append("Зарегистрированные пользователи:\n");

        chatMemberService.findChatMembers(chatId).ifPresentOrElse(
                chatMembers -> chatMembers.forEach(chatMember ->
                        responseText.append(chatMember.getMember().getNickName()).append("\n")),
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
}
