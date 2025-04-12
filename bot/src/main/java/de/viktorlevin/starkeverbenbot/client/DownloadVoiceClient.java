package de.viktorlevin.starkeverbenbot.client;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "download-voice-client", url = "${download-voice-api.url}")
public interface DownloadVoiceClient {

    @GetMapping("/download")
    Response downloadVoice(@RequestParam("wort") String wort);
}
