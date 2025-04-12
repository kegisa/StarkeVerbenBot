package de.viktorlevin.voicedownloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class VoiceDownloaderApplication

fun main(args: Array<String>) {
    runApplication<VoiceDownloaderApplication>(*args)
}
