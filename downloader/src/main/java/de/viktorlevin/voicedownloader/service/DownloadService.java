package de.viktorlevin.voicedownloader.service;

import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import de.viktorlevin.voicedownloader.client.DownloadClient;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.io.IOUtils.toByteArray;

@Slf4j
@Service
@RequiredArgsConstructor
public class DownloadService {
    private final DownloadClient downloadClient;
    private static final String MP3_LINK_START = "<a class=\"\" href=\"";
    private final ConcurrentHashMap<String, byte[]> filesCache = new ConcurrentHashMap();

    @SneakyThrows
    public byte[] getVoice(String word) {
        if (filesCache.containsKey(word)) {
            log.info("Getting voice for {} from cache", word);
            log.info("Cache size is {}", filesCache.size());
            return filesCache.get(word);
        }

        log.info("Going to API to get voice for {}", word);
        String url = getUrlForDownloading(word);
        Response response = downloadClient.downloadVoice(URI.create(url));
        byte[] responseArray = toByteArray(response.body().asInputStream());
        filesCache.put(word, responseArray);

        log.info("Cache size is {}", filesCache.size());
        log.info("Voice for {} was successfully downloaded", word);

        return responseArray;
    }

    private String getUrlForDownloading(String word) {
        log.info("Getting URL for {}", word);
        String response = downloadClient.getPage(word);
        int startIndex = response.indexOf(MP3_LINK_START) + MP3_LINK_START.length();
        int finishIndex = response.indexOf(".mp3") + 4;
        response = response.substring(startIndex, finishIndex);
        log.info("URL for {} was successfully retrieved", word);

        return response;
    }
}