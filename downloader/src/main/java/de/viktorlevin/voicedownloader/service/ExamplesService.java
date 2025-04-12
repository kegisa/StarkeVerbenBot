package de.viktorlevin.voicedownloader.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import de.viktorlevin.voicedownloader.client.ExamplesClient;
import de.viktorlevin.voicedownloader.dto.ExamplesDto;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamplesService {
    private final ExamplesClient examplesClient;
    private static final String LANGUAGE = "de-ru";
    private final Random random = new Random();

    public ExamplesDto.Example getExamples(String word) {
        log.info("Getting examples for {}", word);
        ExamplesDto examples = examplesClient.getExamples(word, LANGUAGE);
        log.info("Examples for {} successfully received", word);
        List<ExamplesDto.Example> examplesList = examples.getResult().get(0).getExamples();
        return examplesList.get(random.nextInt(examplesList.size()));
    }
}
