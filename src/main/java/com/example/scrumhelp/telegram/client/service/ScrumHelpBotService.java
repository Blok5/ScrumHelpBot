package com.example.scrumhelp.telegram.client.service;

import com.example.scrumhelp.scrum.service.*;
import com.example.scrumhelp.telegram.client.enums.DailyReminderState;
import com.example.scrumhelp.telegram.client.exception.NotFoundException;
import com.example.scrumhelp.scrum.model.ChatMember;
import com.example.scrumhelp.scrum.model.Member;
import eye2web.modelmapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.example.scrumhelp.telegram.client.enums.Emoji.*;
import static java.lang.String.format;

@Service
@Slf4j
public class ScrumHelpBotService {
    private final ChatMemberService chatMemberService;
    private final MemberService memberService;
    private final ChatService chatService;
    private final ModelMapper modelMapper;

    @Autowired
    public ScrumHelpBotService(ChatService chatService,
                               ChatMemberService chatMemberService,
                               MemberService memberService,
                               ModelMapper modelMapper) {
        this.chatService = chatService;
        this.chatMemberService = chatMemberService;
        this.memberService = memberService;
        this.modelMapper = modelMapper;
    }

    public SendMessage sendRegisterUserMessage(Long chatId, User fromUser) {
        SendMessage sendMessage = new SendMessage();
        Member member = memberService.findOrCreate(modelMapper.map(fromUser, Member.class));

        chatMemberService.findChatMemberForChat(chatId, member.getId()).ifPresentOrElse(
                chatMember -> {
                    sendMessage.setText(format("Пользователь %s уже зарегистрирован!%s", member.getNickName(), OkHad));
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
        List<ChatMember> chatMembers = chatMemberService.findChatMembers(chatId);
        List<List<InlineKeyboardButton>> keyboardButtonsRowList = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();

        if (!chatMembers.isEmpty()) {
            for (ChatMember chatMember : chatMembers) {
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

    /**
     * Choose new facilitator and send message. If newFacilitatorId null then select randomly
     *
     * @param chatId - chat id
     * @return - message
     */
    public SendMessage sendSetFacilitatorSelectedMessage(Long chatId) {
        List<ChatMember> chatMembers = chatMemberService.findChatMembersExceptFacilitator(chatId);
        ChatMember newFacilitator = chatMembers.get(new Random().nextInt(chatMembers.size()));

        Long newFacilitatorId = newFacilitator.getMember().getId();

        ChatMember chatMember = chatMemberService.changeAndGetNewFacilitatorForChat(chatId, newFacilitatorId)
                .orElseThrow(() -> {
                    log.error("Chat member with id {} not found!", newFacilitatorId);
                    return new NotFoundException("Chat member with id " + newFacilitatorId + " not found!");
                });

        log.info("For chat {} selected new facilitator {}", chatId, chatMember.getMember().getNickName());
        return new SendMessage(chatId.toString(), "Lucky выбрал следующего фасилитатора: " +
                chatMember.getMember().getNickName());
    }

    /**
     * Choose new facilitator and send message. If newFacilitatorId null then select randomly
     *
     * @param chatId - chat id
     * @param update - update from telegram, if null then select randomly
     * @return - message
     */
    public SendMessage sendSetFacilitatorSelectedMessage(Long chatId, Update update) {
        Long newFacilitatorId = Long.parseLong(update.getCallbackQuery().getData().split(" ")[1]);

        Member fromMember = memberService.findOrCreate(
                modelMapper.map(update.getCallbackQuery().getFrom(), Member.class)
        );

        ChatMember chatMember = chatMemberService.changeAndGetNewFacilitatorForChat(chatId, newFacilitatorId)
                .orElseThrow(() -> {
                    log.error("Chat member with id {} not found!", newFacilitatorId);
                    return new NotFoundException("Chat member with id " + newFacilitatorId + " not found!");
                });

        log.info("For chat {} selected new facilitator {}", chatId, chatMember.getMember().getNickName());
        return new SendMessage(chatId.toString(), fromMember.getNickName() +
                " выбрал следующего фасилитатора: " + chatMember.getMember().getNickName());
    }

    public SendMessage sendUserListMessage(Long chatId) {
        StringBuilder responseText = new StringBuilder();
        responseText.append("Зарегистрированные пользователи:\n");

        List<ChatMember> chatMembers = chatMemberService.findChatMembers(chatId);

        if (!chatMembers.isEmpty()) {
            chatMembers.forEach(chatMember -> responseText.append(chatMember.getMember().getNickName()).append("\n"));
        } else {
            responseText.append("Список пуст");
        }

        return new SendMessage(chatId.toString(), responseText.toString());
    }

    public SendMessage sendHelpMessage(Long chatId) {
        return new SendMessage(chatId.toString(),
                "Список возможных команд:\n" +
                        "/register - регистрация пользователя\n" +
                        "/getUserList - список участников\n" +
                        "/setFacilitator - выбор фасилитатора\n" +
                        "/luckyFacilitator - случайный выбор фасилитатора\n" +
                        "/enableDailyReminder - включить напоминание о дейли\n" +
                        "/disableDailyReminder- выключить напоминание о дейли\n"
        );
    }

    public EditMessageReplyMarkup removeMarkupFromPreviousMessage(Long chatId, Integer messageId) {
        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId.toString());
        editMessageReplyMarkup.setMessageId(messageId);
        return editMessageReplyMarkup;
    }
}