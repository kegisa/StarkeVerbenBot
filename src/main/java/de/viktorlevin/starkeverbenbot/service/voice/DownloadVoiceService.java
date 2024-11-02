package de.viktorlevin.starkeverbenbot.service.voice;

import de.viktorlevin.starkeverbenbot.client.DownloadVoiceClient;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadVoiceService {
    private final DownloadVoiceClient downloadVoiceClient;

    @SneakyThrows
    public InputStream getVoiceForWord(String word) {
        Response response = downloadVoiceClient.downloadVoice(word);
        return response.body().asInputStream();
    }
}