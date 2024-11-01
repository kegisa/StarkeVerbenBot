package de.viktorlevin.starkeverbenbot.service.telegram;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeyboadService {
    public SendMessage addKeyBoardMarkup(SendMessage sendMessage) {
        KeyboardRow kr = new KeyboardRow(List.of(
                KeyboardButton.builder()
                        .text("ein Wort bitte")
                        .build(),
                KeyboardButton.builder()
                        .text("ein starkes Verb bitte")
                        .build()));
        if (sendMessage.getReplyMarkup() == null) {
            sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder()
                    .keyboard(List.of(kr))
                    .resizeKeyboard(true)
                    .build());
        }
        return sendMessage;
    }

    public SendMessage addInlineKeyBoard(SendMessage wordMessage, Integer wordId) {
        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("Убрать слово! Я запомнил")
                .callbackData(Integer.toString(wordId)).build();

        wordMessage.setReplyMarkup(InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(button))
                .build());
        return wordMessage;
    }
}