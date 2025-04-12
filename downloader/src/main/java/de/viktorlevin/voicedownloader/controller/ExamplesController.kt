package de.viktorlevin.voicedownloader.controller

import de.viktorlevin.voicedownloader.dto.ExamplesDto
import de.viktorlevin.voicedownloader.service.ExampleService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/examples")
class ExamplesController(private val examplesService: ExampleService) {
    @GetMapping("/one")
    fun getExample(@RequestParam("word") word: String): ExamplesDto.Example {
        return examplesService.getExamples(word)
    }
}