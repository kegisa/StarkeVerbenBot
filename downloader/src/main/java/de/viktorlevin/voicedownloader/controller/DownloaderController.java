package de.viktorlevin.voicedownloader.controller;

import de.viktorlevin.voicedownloader.service.DownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/download")
@RequiredArgsConstructor
public class DownloaderController {
    private final DownloadService downloadService;

    @GetMapping
    public byte[] getVoice(@RequestParam("wort") String word) {
        return downloadService.getVoice(word);
    }
}
