package de.viktorlevin.starkeverbenbot.service.alltypes;

import de.viktorlevin.starkeverbenbot.service.StarkeVerbenService;
import de.viktorlevin.starkeverbenbot.service.WortService;
import de.viktorlevin.starkeverbenbot.service.telegram.MessageService;
import de.viktorlevin.starkeverbenbot.service.voice.DownloadVoiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoiceMessageService {

    private final DownloadVoiceService downloadVoiceService;
    private final MessageService messageService;
    private final StarkeVerbenService starkeVerbenService;
    private final WortService wortService;


    public SendVoice processVoiceCallback(CallbackQuery callbackQuery) {
        log.info("Got CallbackVoice message {} from {} with username {}",
                callbackQuery.getData(),
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getFrom().getUserName());

        String id = callbackQuery.getData().split(":")[1];
        if (callbackQuery.getData().contains("Verb")) {
            return getVoiceForVerb(id, callbackQuery.getMessage().getChatId());
        } else if (callbackQuery.getData().contains("Word")) {
            return getVoiceWord(id, callbackQuery.getMessage().getChatId());
        } else {
            log.error("Callback voice data was not recognized {}", callbackQuery.getData());
            throw new IllegalStateException("Что-то пошло не так...");
        }
    }

    private SendVoice getVoiceForVerb(String id, Long chatId) {
        String verb = starkeVerbenService.findStarkesVerbById(Integer.valueOf(id)).getInfinitiv();
        InputStream inputStream = downloadVoiceService.getVoiceForWord(verb);
        return messageService.createVoiceMessage(inputStream, chatId, verb);
    }

    private SendVoice getVoiceWord(String id, Long chatId) {
        String word = wortService.findWortById(Integer.valueOf(id)).getWord();

        String readyWord = checkAndPrepareWord(word);
        InputStream inputStream = downloadVoiceService.getVoiceForWord(readyWord);
        return messageService.createVoiceMessage(inputStream, chatId, word);
    }

    private String checkAndPrepareWord(String word) {
        if (word.split(" ").length > 2 || word.contains(".")) {
            throw new RuntimeException("Не могу озвучить это слово...");
        }
        return word.replace("der ", "");
    }
}