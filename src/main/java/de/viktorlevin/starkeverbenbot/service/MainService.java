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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
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
            try {
                return List.of(processTextMessage(update.getMessage()));
            } catch (Exception exception) {
                return List.of(textService.createMessage(update.getMessage().getChatId(), exception.getMessage()));
            }
        }
        if (update.hasCallbackQuery()) {
            try {
                return processCallbackQuery(update.getCallbackQuery());
            } catch (Exception exception) {
                return List.of(textService.createMessage(update.getCallbackQuery().getFrom().getId(), exception.getMessage()));
            }
        }
        log.error("Could not process this message {}", update.getMessage());
        throw new IllegalStateException("Could not process this message");
    }


    public SendMessage processTextMessage(Message message) {
        String userName = message.getChat().getUserName();
        Long chatId = message.getChat().getId();
        String messageText = message.getText();

        log.info("Got text message from {}", userName);
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
                return keyboadService.addInlineKeyBoard(wordMessage, wort.getId());
            default:
                throw new IllegalStateException("Я не понимаю, что мне делать...");
        }
    }


    private List<BotApiMethod> processCallbackQuery(CallbackQuery callbackQuery) {
        wortService.markWordAsLearned(callbackQuery.getData(),
                callbackQuery.getFrom().getId(),
                callbackQuery.getFrom().getUserName());

        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();

        var deleteKeybord = messageService.createEditMessageReplyMarkup(chatId, messageId);
        var sayUserThatMarkedWord = textService.markedWordAsLearned(chatId);

        return List.of(deleteKeybord, sayUserThatMarkedWord);
    }

    private boolean isTextMessage(Update update) {
        return update.getMessage().hasText();
    }
}