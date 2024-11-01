package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedWort;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import de.viktorlevin.starkeverbenbot.repository.LearnedWordsRepository;
import de.viktorlevin.starkeverbenbot.repository.WortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WortService {
    private final WortRepository wortRepository;
    private final LearnedWordsRepository learnedWordsRepository;

    @Value("${words.wordsAtTheMoment}")
    private Long wordsAtTheMoment;

    @Transactional(readOnly = true)
    public Wort getRandomWort(BotUser user) {
        return learnedWordsRepository.getRandomWort(user.getId()).getWort();
    }

    @Transactional
    public void initializeLearningProcess(BotUser user) {
        log.info("Initializing learning process for {}", user.getUsername());
        if (learnedWordsRepository.countByUser(user) >= wordsAtTheMoment) {
            log.info("Learning process already was initiated for user {}", user.getUsername());
            return;
        }

        List<Wort> firstRandomWords = wortRepository.getFirstNRandomWords(wordsAtTheMoment);
        List<LearnedWort> firstWordsToLearn = firstRandomWords.stream()
                .map(wort -> new LearnedWort(user, wort, LearnedWort.Status.IN_PROGRESS))
                .toList();
        learnedWordsRepository.saveAll(firstWordsToLearn);
        log.info("Initializing was processed for {} successfully", user.getUsername());
    }
}