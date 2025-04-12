package de.viktorlevin.voicedownloader.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;

@FeignClient(name = "download-voice-client", url = "${download-voice-api.url}")
public interface DownloadClient {
    @GetMapping
    String getPage(@RequestParam("w") String wort);

    @GetMapping
    Response downloadVoice(URI baseUrl);
}
