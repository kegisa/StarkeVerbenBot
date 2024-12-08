package de.viktorlevin.starkeverbenbot.bot;

import de.viktorlevin.starkeverbenbot.configuration.BotConfig;
import de.viktorlevin.starkeverbenbot.service.TextService;
import de.viktorlevin.starkeverbenbot.service.alltypes.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class StarkeVerbenBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final TextService textService;
    private final CallbackService callbackService;
    private final TextMessageService textMessageService;
    private final VoiceMessageService voiceMessageService;
    private final ChatService chatService;
    private final ExampleService exampleService;

    public StarkeVerbenBot(BotConfig config, TextService textService, CallbackService callbackService,
                           TextMessageService textMessageService, VoiceMessageService voiceMessageService,
                           ChatService chatService, ExampleService exampleService) {
        super(config.getToken());
        this.config = config;
        this.textService = textService;
        this.callbackService = callbackService;
        this.textMessageService = textMessageService;
        this.voiceMessageService = voiceMessageService;
        this.chatService = chatService;
        this.exampleService = exampleService;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            routeUpdate(update);
        } catch (Exception exception) {
            log.error(exception.toString());
            log.info(update.toString());
        }
    }

    private void routeUpdate(Update update) {
        if (update.hasMessage() && isTextMessage(update)) {
            sendApiMethodToUser(textMessageService.processTextMessage(update.getMessage()));
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("voice")) {
            try {
                sendVoiceMessage(voiceMessageService.processVoiceCallback(update.getCallbackQuery()));
            } catch (Exception exception) {
                sendApiMethodToUser(List.of(textService.createMessage(
                        update.getCallbackQuery().getMessage().getChatId(),
                        exception.getMessage())));
            }
        } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData().contains("example")) {
            try {
                sendApiMethodToUser(exampleService.getExample(update.getCallbackQuery()));
            } catch (Exception exception) {
                sendApiMethodToUser(List.of(textService.createMessage(
                        update.getCallbackQuery().getMessage().getChatId(),
                        exception.getMessage())));
            }
        } else if (update.hasCallbackQuery()) {
            sendApiMethodToUser(callbackService.processCallbackQuery(update.getCallbackQuery()));
        } else if (update.hasMyChatMember()) {
            sendApiMethodToUser(chatService.processChatMemberMessage(update.getMyChatMember()));
        } else if (update.hasMessage() && update.getMessage().getLeftChatMember() != null) {
            chatService.oneMemberLeft(update.getMessage());
        } else {
            log.error("Could not process this message {}", update);
            throw new IllegalStateException("Я не знаю, что мне делать...");
        }
    }

    public void sendApiMethodToUser(List<BotApiMethod> apiMethods) {
        apiMethods.forEach(apiMethod -> {
            try {
                execute(apiMethod);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("SendMessage sent");
    }

    @SneakyThrows
    public void sendVoiceMessage(SendVoice sendVoice) {
        execute(sendVoice);
        log.info("Voice message sent");
    }

    private boolean isTextMessage(Update update) {
        return update.getMessage().hasText();
    }
}
