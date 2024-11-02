package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedStarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.repository.LearnedStarkeVerbenRepository;
import de.viktorlevin.starkeverbenbot.repository.StarkesVerbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StarkeVerbenService {
    private final StarkesVerbRepository starkesVerbRepository;
    private final LearnedStarkeVerbenRepository learnedStarkeVerbenRepository;
    private final UserService userService;

    @Value("${verbs.verbsAtTheMoment}")
    private Long verbsAtTheMoment;

    @Transactional
    public StarkesVerb getRandomStarkesVerb(BotUser user) {
        LearnedStarkesVerb randomStarkesVerb = learnedStarkeVerbenRepository.getRandomStarkesVerb(user.getId())
                .orElseGet(() -> {
                    if (learnedStarkeVerbenRepository.countByUserAndStatus(user, LearnedStarkesVerb.Status.FINISHED) == 0) {
                        initializeLearningProcess(user);
                        return learnedStarkeVerbenRepository.getRandomStarkesVerb(user.getId()).get();
                    }
                    throw new IllegalStateException("Вы выучили все сильные глаголы из списка!");
                });
        return randomStarkesVerb.getVerb();
    }

    @Transactional
    public void initializeLearningProcess(BotUser user) {
        log.info("Initializing verbs learning process for {}", user.getChatId());
        if (learnedStarkeVerbenRepository.countByUser(user) > 0) {
            log.info("Verbs learning process already was initiated for user {}", user.getChatId());
            return;
        }

        List<StarkesVerb> firstRandomVerbs = starkesVerbRepository.getFirstNRandomVerbs(verbsAtTheMoment);
        List<LearnedStarkesVerb> firstVerbsToLearn = firstRandomVerbs.stream()
                .map(verb -> new LearnedStarkesVerb(user, verb, LearnedStarkesVerb.Status.IN_PROGRESS))
                .toList();
        learnedStarkeVerbenRepository.saveAll(firstVerbsToLearn);
        log.info("Initializing of learning verbs was processed for {} successfully", user.getChatId());
    }

    @Transactional
    public void markVerbAsLearned(String callbackData, Long chatId, String userName) {
        Integer verbId = Integer.valueOf(callbackData.split(":")[1]);
        BotUser user = userService.registrateUser(chatId, userName);

        learnedStarkeVerbenRepository.findByUserAndVerb_Id(user, verbId)
                .ifPresent(learnedVerb -> learnedVerb.setStatus(LearnedStarkesVerb.Status.FINISHED));
        long learnedVerbsCount = learnedStarkeVerbenRepository.countByUserAndStatus(user, LearnedStarkesVerb.Status.IN_PROGRESS);
        if (learnedVerbsCount < verbsAtTheMoment) {
            fillLearningVerbs(verbsAtTheMoment - learnedVerbsCount, user);
        }
    }

    @Transactional(readOnly = true)
    public StarkesVerb findStarkesVerbById(Integer id) {
        return starkesVerbRepository.findById(id).orElseThrow(() -> new RuntimeException("Что то пошло не так"));
    }

    private void fillLearningVerbs(long times, BotUser user) {
        for (int i = 0; i < times; i++) {
            starkesVerbRepository.getNewVerbForLearning(user.getId()).ifPresent(verb ->
                    learnedStarkeVerbenRepository.save(new LearnedStarkesVerb(user, verb, LearnedStarkesVerb.Status.IN_PROGRESS))
            );
        }
    }
}
