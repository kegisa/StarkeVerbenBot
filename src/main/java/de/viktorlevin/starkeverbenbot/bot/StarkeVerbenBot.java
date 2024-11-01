package de.viktorlevin.starkeverbenbot.bot;

import de.viktorlevin.starkeverbenbot.configuration.BotConfig;
import de.viktorlevin.starkeverbenbot.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
public class StarkeVerbenBot extends TelegramLongPollingBot {
    private final BotConfig config;
    private final MainService mainService;

    public StarkeVerbenBot(BotConfig config, MainService mainService) {
        super(config.getToken());
        this.config = config;
        this.mainService = mainService;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        sendToUser(mainService.process(update));
    }

    private void sendToUser(List<BotApiMethod> apiMethods) {
        apiMethods.forEach(apiMethod -> {
            try {
                execute(apiMethod);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        });
        log.info("Reply sent");
    }
}
