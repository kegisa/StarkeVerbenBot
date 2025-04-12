package de.viktorlevin.voicedownloader.controller;


import de.viktorlevin.voicedownloader.service.ExamplesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.viktorlevin.voicedownloader.dto.ExamplesDto;

@Slf4j
@RestController
@RequestMapping("/examples")
@RequiredArgsConstructor
public class ExamplesController {
    private final ExamplesService examplesService;

    @GetMapping("/one")
    public ExamplesDto.Example getExamples(@RequestParam String word) {
        return examplesService.getExamples(word);
    }
}
