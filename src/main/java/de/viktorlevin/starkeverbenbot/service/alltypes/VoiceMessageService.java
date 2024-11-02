package de.viktorlevin.starkeverbenbot.service.alltypes;

import de.viktorlevin.starkeverbenbot.service.StarkeVerbenService;
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


    public SendVoice processVoiceCallback(CallbackQuery callbackQuery) {
        String id = callbackQuery.getData().split(":")[1];
        if (callbackQuery.getData().contains("Verb")) {
            return getVoiceForVerb(id, callbackQuery.getMessage().getChatId());
        } else {
            return getVoiceWord(id);
        }
    }

    private SendVoice getVoiceForVerb(String id, Long chatId) {
        String verb = starkeVerbenService.findStarkesVerbById(Integer.valueOf(id)).getInfinitiv();
        InputStream inputStream = downloadVoiceService.getVoiceForWord(verb);
        return messageService.createVoiceMessage(inputStream, chatId, verb);
    }

    private SendVoice getVoiceWord(String id) {
        return null;
    }
}