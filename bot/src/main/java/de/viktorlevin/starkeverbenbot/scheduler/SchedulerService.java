package de.viktorlevin.starkeverbenbot.scheduler;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.service.NotificationService;
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
    @Value("${notifications.top.time}")
    private int lastHours;
    @Value("${notifications.top.size}")
    private int topSize;

    @Scheduled(cron = "${notifications.cron}")
    public void sendNotificationAboutActivity() {
        log.info("Getting users for sending activity notifications");
        List<BotUser> users = notificationService.getUsersWithoutActivity(millsWithoutActivity);
        log.info("{} users were received for activity notifications", users.size());
        notificationService.sendActivityNotifications(users);
        log.info("Notifications were successfully sent");
    }

    @Scheduled(cron = "${notifications.top.cron}")
    public void sendNotificationAboutLastWeek() {
        log.info("Getting users for sending top notification");
        List<BotUser> users = notificationService.getTopActiveUsersForLastNHours(topSize, lastHours);
        log.info("{} users were received for top notification", users.size());
        notificationService.sendTopNotifications(users, topSize, lastHours);
        log.info("Top notifications were successfully sent");
    }
}
