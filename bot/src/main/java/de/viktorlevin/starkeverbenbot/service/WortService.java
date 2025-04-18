package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedWort;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import de.viktorlevin.starkeverbenbot.repository.LearnedWordsRepository;
import de.viktorlevin.starkeverbenbot.repository.WortRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class WortService {
    private final WortRepository wortRepository;
    private final LearnedWordsRepository learnedWordsRepository;
    private final UserService userService;
    private final RandomDataGenerator random;

    @Value("${words.wordsAtTheMoment}")
    private Long wordsAtTheMoment;


    @Transactional
    public Wort getRandomWort(BotUser user) {
        List<LearnedWort> wordsInProcess = learnedWordsRepository.findAllByUserAndStatus(
                user, LearnedWort.Status.IN_PROGRESS);
        if (wordsInProcess == null || wordsInProcess.isEmpty()) {
            if (learnedWordsRepository.countByUserAndStatus(user, LearnedWort.Status.FINISHED) == 0) {
                initializeLearningProcess(user);
                return getRandomWortFromList(learnedWordsRepository.findAllByUserAndStatus(user, LearnedWort.Status.IN_PROGRESS));
            }
            log.info("User {} finished with worte", user.getChatId());
            Long countFinished = learnedWordsRepository.usersFinished();
            throw new IllegalStateException("Вы выучили все слова из списка!\uD83C\uDFC6 Таких пользователей всего %s, поздравляю! \uD83C\uDF89 Дайте мне немного времени, в ближайшее время я добавлю Вам еще 2000 слов в Ваш словарь\uD83D\uDCDA. Спасибо, что пользуетесь!\uD83D\uDD25".formatted(String.valueOf(countFinished)));
        } else {
            return getRandomWortFromList(wordsInProcess);
        }
    }

    private Wort getRandomWortFromList(List<LearnedWort> words) {
        int i = random.nextInt(0, words.size() - 1);
        return words.get(i).getWort();
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

    public Wort findWortById(Integer id) {
        return wortRepository.findById(id).orElseThrow(() -> new RuntimeException("Что то пошло не так"));
    }

    private void fillLearningWords(long times, BotUser user) {
        for (int i = 0; i < times; i++) {
            wortRepository.getNewWordForLearning(user.getId()).ifPresent(word ->
                    learnedWordsRepository.save(new LearnedWort(user, word, LearnedWort.Status.IN_PROGRESS))
            );
        }
    }

    public long getQuantityOfLearnedWords(BotUser user) {
        log.info("Getting quantity of learned words for chatId {} and username {}", user.getChatId(), user.getUsername());
        return learnedWordsRepository.countByUserAndStatus(user, LearnedWort.Status.FINISHED);
    }

    public long getQuantityOfLearnedWordsForLastNHours(BotUser user, int nHours) {
        log.info("Getting quantity of learned words for chatId {} and username {} for last {} hours", user.getChatId(), user.getUsername(), nHours);
        Long count = learnedWordsRepository.countByUserAndStatusForLastNHours(user.getId(), nHours);
        return Objects.isNull(count) ? 0 : count;
    }
}