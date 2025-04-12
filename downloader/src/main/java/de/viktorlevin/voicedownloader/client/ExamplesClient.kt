package de.viktorlevin.voicedownloader.client

import de.viktorlevin.voicedownloader.dto.ExamplesDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "examples-client", url = "\${examples-api.url}")
interface ExamplesClient {
    @GetMapping
    fun getExamples(@RequestParam("src") word: String, @RequestParam("lang") language: String): ExamplesDto

}
