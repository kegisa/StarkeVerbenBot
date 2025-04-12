package de.viktorlevin.voicedownloader.controller

import de.viktorlevin.voicedownloader.service.DownloadService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/download")
class VoiceDownloadController(private val downloadService: DownloadService) {
    @GetMapping
    fun getVoice(@RequestParam("wort") word: String): ByteArray? {
        return downloadService.getVoice(word)
    }
}