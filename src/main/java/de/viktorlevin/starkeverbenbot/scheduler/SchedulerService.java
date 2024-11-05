package de.viktorlevin.starkeverbenbot.scheduler;

import de.viktorlevin.starkeverbenbot.bot.StarkeVerbenBot;
import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.service.NotificationService;
import de.viktorlevin.starkeverbenbot.service.StatisticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final NotificationService notificationService;

    @Value("${notifications.activity}")
    private long millsWithoutActivity;

    @Scheduled(fixedDelay = 30000, initialDelay = 10_000)
    public void sendNotificationAboutActivity() {
        log.info("Getting users for sending notifications");
        List<BotUser> users = notificationService.getUsersWithoutActivity(millsWithoutActivity);
        log.info("{} users were received for notifications", users.size());
        notificationService.sendNotifications(users);
        log.info("Notifications were successfully sent");
    }
}