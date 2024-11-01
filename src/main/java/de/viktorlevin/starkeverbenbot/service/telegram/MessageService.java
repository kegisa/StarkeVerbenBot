package de.viktorlevin.starkeverbenbot.service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    public SendMessage createMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public SendMessage createMessageWithEntites(Long chatId, String text, List<MessageEntity> entities) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .entities(entities)
                .build();
    }

    public EditMessageReplyMarkup createEditMessageReplyMarkup(Long chatId, Integer messageId) {
        return EditMessageReplyMarkup.builder()
                .chatId(chatId)
                .replyMarkup(null)
                .messageId(messageId)
                .build();
    }
}