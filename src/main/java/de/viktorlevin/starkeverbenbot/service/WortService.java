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
    private final UserService userService;

    @Value("${words.wordsAtTheMoment}")
    private Long wordsAtTheMoment;

    @Transactional
    public Wort getRandomWort(BotUser user) {
        LearnedWort randomWort = learnedWordsRepository.getRandomWort(user.getId())
                .orElseGet(() -> {
                    if (learnedWordsRepository.countByUserAndStatus(user, LearnedWort.Status.FINISHED) == 0) {
                        initializeLearningProcess(user);
                        return learnedWordsRepository.getRandomWort(user.getId()).get();
                    }
                    throw new IllegalStateException("Вы выучили все слова из списка!");
                });
        return randomWort.getWort();
    }

    @Transactional
    public void initializeLearningProcess(BotUser user) {
        log.info("Initializing words learning process for {}", user.getChatId());
        if (learnedWordsRepository.countByUser(user) > 0) {
            log.info("Words learning process already was initiated for user {}", user.getChatId());
            return;
        }

        List<Wort> firstRandomWords = wortRepository.getFirstNRandomWords(wordsAtTheMoment);
        List<LearnedWort> firstWordsToLearn = firstRandomWords.stream()
                .map(wort -> new LearnedWort(user, wort, LearnedWort.Status.IN_PROGRESS))
                .toList();
        learnedWordsRepository.saveAll(firstWordsToLearn);
        log.info("Initializing of learning words was processed for {} successfully", user.getChatId());
    }

    @Transactional
    public void markWordAsLearned(String callbackData, Long chatId, String userName) {
        Integer wordId = Integer.valueOf(callbackData.split(":")[1]);
        BotUser user = userService.registrateUser(chatId, userName);

        learnedWordsRepository.findByUserAndWort_Id(user, wordId)
                .ifPresent(learnedWort -> learnedWort.setStatus(LearnedWort.Status.FINISHED));
        long learnedWordsCount = learnedWordsRepository.countByUserAndStatus(user, LearnedWort.Status.IN_PROGRESS);
        if (learnedWordsCount < wordsAtTheMoment) {
            fillLearningWords(wordsAtTheMoment - learnedWordsCount, user);
        }
    }

    private void fillLearningWords(long times, BotUser user) {
        for (int i = 0; i < times; i++) {
            wortRepository.getNewWordForLearning(user.getId()).ifPresent(word ->
                    learnedWordsRepository.save(new LearnedWort(user, word, LearnedWort.Status.IN_PROGRESS))
            );
        }
    }
}