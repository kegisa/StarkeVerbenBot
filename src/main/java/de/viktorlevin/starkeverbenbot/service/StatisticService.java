package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.StatisticRequest;
import de.viktorlevin.starkeverbenbot.entity.UserStatistic;
import de.viktorlevin.starkeverbenbot.repository.StatisticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticService {

    private final StatisticRepository statisticRepository;
    private final WortService wortService;
    private final StarkeVerbenService starkeVerbenService;

    @Transactional
    public void saveRequestToStatistic(BotUser user, String messageText) {
        statisticRepository.save(new StatisticRequest(user, messageText));
    }

    public UserStatistic getUserStatistic(BotUser user) {
        return UserStatistic.builder()
                .chatId(user.getChatId())
                .learnedWords(wortService.getQuantityOfLearnedWords(user))
                .learnedStarkesVerbs(starkeVerbenService.getQuantityOfLearnedVerbs(user))
                .requests(statisticRepository.countByUser(user))
                .build();
    }
}
