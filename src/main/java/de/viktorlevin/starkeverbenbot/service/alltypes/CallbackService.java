package de.viktorlevin.starkeverbenbot.service.alltypes;

import de.viktorlevin.starkeverbenbot.service.StarkeVerbenService;
import de.viktorlevin.starkeverbenbot.service.TextService;
import de.viktorlevin.starkeverbenbot.service.WortService;
import de.viktorlevin.starkeverbenbot.service.telegram.KeyboadService;
import de.viktorlevin.starkeverbenbot.service.telegram.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CallbackService {
    private final WortService wortService;
    private final TextService textService;
    private final StarkeVerbenService starkeVerbenService;
    private final MessageService messageService;
    private final KeyboadService keyboadService;

    public List<BotApiMethod> processCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String userName = callbackQuery.getFrom().getUserName();
        try {

            log.info("Got Callback message {} from {} with username {}", callbackQuery.getData(), chatId, userName);

            BotApiMethod callbackResponse = null;

            if (callbackQuery.getData().contains("word")) {
                wortService.markWordAsLearned(callbackQuery.getData(),
                        callbackQuery.getMessage().getChatId(),
                        callbackQuery.getFrom().getUserName());
                callbackResponse = textService.markedWordAsLearned(chatId);
            } else {
                starkeVerbenService.markVerbAsLearned(callbackQuery.getData(),
                        callbackQuery.getMessage().getChatId(),
                        callbackQuery.getFrom().getUserName());
                callbackResponse = textService.markedVerbAsLearned(chatId);
            }

            var deleteKeybord = messageService.createEditMessageReplyMarkup(chatId, messageId);

            return List.of(deleteKeybord, callbackResponse);
        } catch (Exception exception) {
            log.error(exception.toString());
            return List.of(textService.createMessage(chatId, exception.getMessage()));
        }
    }
}