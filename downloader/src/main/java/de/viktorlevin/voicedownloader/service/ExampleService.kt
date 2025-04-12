package de.viktorlevin.voicedownloader.service

import de.viktorlevin.voicedownloader.client.ExamplesClient
import de.viktorlevin.voicedownloader.dto.ExamplesDto
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*

@Slf4j
@Service
class ExampleService(private val examplesClient: ExamplesClient) {
    private val random: Random = Random()

    companion object {
        const val LANGUAGE = "de-ru"
    }

    private val log: Logger = LoggerFactory.getLogger(ExampleService::class.java)

    fun getExamples(word: String): ExamplesDto.Example {
        log.info("Getting examples for {}", word)
        val (result) = examplesClient.getExamples(word, LANGUAGE)
        log.info("Examples for {} successfully received", word)
        val examplesList = result[0].examples
        return examplesList[random.nextInt(examplesList.size)]
    }
}