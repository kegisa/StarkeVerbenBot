package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainService {
    private final UserService userService;
    private final TextService textService;
    private final StarkeVerbenService starkeVerbenService;
    private final WortService wortService;
    private final StatisticService statisticService;

    public SendMessage process(Update update) {
        if (update.hasMessage() && isTextMessage(update)) {
            log.info("Got text message from {}", update.getMessage().getChat().getUserName());
            SendMessage sendMessage = processTextMessage(
                    update.getMessage().getText(),
                    update.getMessage().getChatId(),
                    update.getMessage().getChat().getUserName());
            return addKeyBoard(sendMessage);
        }
        return textService.unexpectedMessage(update.getMessage().getChatId());
    }

    private SendMessage addKeyBoard(SendMessage sendMessage) {
        KeyboardRow kr = new KeyboardRow(List.of(
                KeyboardButton.builder()
                        .text("ein Wort bitte")
                        .build(),
                KeyboardButton.builder()
                        .text("ein Verb bitte")
                        .build()));
        sendMessage.setReplyMarkup(ReplyKeyboardMarkup.builder()
                .keyboard(List.of(kr))
                .resizeKeyboard(true)
                .build());
        return sendMessage;
    }

    public SendMessage processTextMessage(String messageText, Long chatId, String userName) {
        BotUser user = userService.registrateUser(chatId, userName);
        statisticService.saveRequestToStatistic(user, messageText);

        log.info("Text is {}", messageText);

        switch (messageText) {
            case "/start":
                wortService.initializeLearningProcess(user);
                return textService.startBot(chatId);
            case "/einVerb", "ein Verb bitte":
                StarkesVerb starkesVerb = starkeVerbenService.getRandomStarkesVerb();
                return textService.generateStarkesVerb(starkesVerb, chatId);
            case "/einWort", "ein Wort bitte":
                Wort wort = wortService.getRandomWort(user);
                return textService.generateWort(wort, chatId);
            default:
                log.info("Unexpected message");
                return textService.unexpectedMessage(chatId);
        }
    }

    private boolean isTextMessage(Update update) {
        return update.getMessage().hasText();
    }
}