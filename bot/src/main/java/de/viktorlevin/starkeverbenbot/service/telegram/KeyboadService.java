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
    private static final String templateCallbackData = "%s:%s";

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

    public SendMessage addInlineKeyBoardForWord(SendMessage wordMessage, Integer wordId) {
        InlineKeyboardButton markButton = InlineKeyboardButton.builder()
                .text("Убрать слово! Я запомнил ✅")
                .callbackData(templateCallbackData.formatted("word", Integer.toString(wordId))).build();
        InlineKeyboardButton voiceButton = InlineKeyboardButton.builder()
                .text("Озвучить \uD83D\uDD0A")
                .callbackData(templateCallbackData.formatted("voiceWord", Integer.toString(wordId))).build();
        InlineKeyboardButton exampleButton = InlineKeyboardButton.builder()
                .text("Пример использования \uD83D\uDCDD")
                .callbackData(templateCallbackData.formatted("exampleWord", Integer.toString(wordId))).build();

        wordMessage.setReplyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(voiceButton), List.of(exampleButton), List.of(markButton)))
                .build());
        return wordMessage;
    }

    public SendMessage addInlineKeyBoardForVerb(SendMessage verbMessage, Integer verbId) {
        InlineKeyboardButton markButton = InlineKeyboardButton.builder()
                .text("Убрать глагол! Я запомнил ✅")
                .callbackData(templateCallbackData.formatted("verb", Integer.toString(verbId))).build();
        InlineKeyboardButton voiceButton = InlineKeyboardButton.builder()
                .text("Озвучить \uD83D\uDD0A")
                .callbackData(templateCallbackData.formatted("voiceVerb", Integer.toString(verbId))).build();
        InlineKeyboardButton exampleButton = InlineKeyboardButton.builder()
                .text("Пример использования \uD83D\uDCDD")
                .callbackData(templateCallbackData.formatted("exampleVerb", Integer.toString(verbId))).build();

        verbMessage.setReplyMarkup(InlineKeyboardMarkup.builder()
                .keyboard(List.of(List.of(voiceButton), List.of(exampleButton), List.of(markButton)))
                .build());
        return verbMessage;
    }
}