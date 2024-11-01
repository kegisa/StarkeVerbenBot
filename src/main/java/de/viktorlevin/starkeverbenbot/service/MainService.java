package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import de.viktorlevin.starkeverbenbot.service.telegram.KeyboadService;
import de.viktorlevin.starkeverbenbot.service.telegram.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
    private final KeyboadService keyboadService;
    private final MessageService messageService;

    public List<BotApiMethod> process(Update update) {
        if (update.hasMessage() && isTextMessage(update)) {
            log.info("Got text message from {}", update.getMessage().getChat().getUserName());
            return List.of(processTextMessage(
                    update.getMessage().getText(),
                    update.getMessage().getChatId(),
                    update.getMessage().getChat().getUserName()));
        }
        if (update.hasCallbackQuery()) {
            //wortService.markWordAsLearned();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
            var deleteKeybord = messageService.createEditMessageReplyMarkup(chatId, messageId);
            var sayUserThatMarkedWord = textService.markedWordAsLearned(chatId);
            return List.of(deleteKeybord, sayUserThatMarkedWord);

        }
        return List.of(textService.unexpectedMessage(update.getMessage().getChatId()));
    }


    public SendMessage processTextMessage(String messageText, Long chatId, String userName) {
        BotUser user = userService.registrateUser(chatId, userName);
        statisticService.saveRequestToStatistic(user, messageText);

        log.info("Text is {}", messageText);

        switch (messageText) {
            case "/start":
                wortService.initializeLearningProcess(user);
                SendMessage startMessage = textService.startBot(chatId);
                return keyboadService.addKeyBoardMarkup(startMessage);
            case "/einVerb", "ein starkes Verb bitte":
                StarkesVerb starkesVerb = starkeVerbenService.getRandomStarkesVerb();
                return textService.generateStarkesVerb(starkesVerb, chatId);
            case "/einWort", "ein Wort bitte":
                Wort wort = wortService.getRandomWort(user);
                SendMessage wordMessage = textService.generateWort(wort, chatId);
                return keyboadService.addInlineKeyBoard(wordMessage);
            default:
                log.info("Unexpected message");
                return textService.unexpectedMessage(chatId);
        }
    }

    private boolean isTextMessage(Update update) {
        return update.getMessage().hasText();
    }
}