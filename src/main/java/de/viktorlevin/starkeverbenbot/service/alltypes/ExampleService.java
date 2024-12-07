package de.viktorlevin.starkeverbenbot.service.alltypes;

import de.viktorlevin.starkeverbenbot.client.ExamplesClient;
import de.viktorlevin.starkeverbenbot.dto.ExamplesDto;
import de.viktorlevin.starkeverbenbot.service.StarkeVerbenService;
import de.viktorlevin.starkeverbenbot.service.TextService;
import de.viktorlevin.starkeverbenbot.service.WortService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExampleService {
    private final ExamplesClient examplesClient;
    private final WortService wortService;
    private final StarkeVerbenService starkeVerbenService;
    private final TextService textService;

    public List<BotApiMethod> getExample(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        String username = callbackQuery.getFrom().getUserName();
        String data = callbackQuery.getData();
        log.info("Got CallbackExample message {} from {} with username {}",
                data, chatId, username);
        try {
            ExamplesDto example = examplesClient.getExample(extractWord(data));
            return List.of(textService.messageWithExample(example, chatId));
        } catch (Exception exception) {
            log.error("error during getting examples", exception);
            throw new RuntimeException("Не могу найти примеры для этого слова...");
        }
    }

    private String extractWord(String data) {
        Integer id = Integer.valueOf(data.split(":")[1]);
        if (data.contains("Verb")) {
            return starkeVerbenService.findStarkesVerbById(id).getPartizip2();
        } else {
            return wortService.findWortById(id).getWord();
        }
    }
}
