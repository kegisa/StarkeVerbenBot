package de.viktorlevin.voicedownloader.service

import de.viktorlevin.voicedownloader.client.DownloadClient
import feign.Response
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.net.URI
import java.util.concurrent.ConcurrentHashMap

@Service
class DownloadService(private val downloadClient: DownloadClient) {

    private val filesCache: ConcurrentHashMap<String, ByteArray> = ConcurrentHashMap()

    companion object {
        const val MP3_LINK_START = "<a class=\"\" href=\""
    }

    private val log: Logger = LoggerFactory.getLogger(DownloadService::class.java)

    fun getVoice(word: String): ByteArray? {
        if (filesCache.containsKey(word)) {
            log.info("Getting voice for {} from cache", word)
            log.info("Cache size is {}", filesCache.size)
            return filesCache[word]
        }

        log.info("Going to API to get voice for {}", word)
        val url: String = getUrlForDownloading(word)
        val response: Response = downloadClient.downloadVoice(URI.create(url))
        val responseArray = IOUtils.toByteArray(response.body().asInputStream())
        filesCache[word] = responseArray

        log.info("Cache size is {}", filesCache.size)
        log.info("Voice for {} was successfully downloaded", word)

        return responseArray
    }

    private fun getUrlForDownloading(word: String): String {
        log.info("Getting URL for {}", word)
        var response = downloadClient.getPage(word)
        val startIndex = response.indexOf(MP3_LINK_START) + MP3_LINK_START.length
        val finishIndex = response.indexOf(".mp3") + 4
        response = response.substring(startIndex, finishIndex)
        log.info("URL for {} was successfully retrieved", word)
        return response
    }
}