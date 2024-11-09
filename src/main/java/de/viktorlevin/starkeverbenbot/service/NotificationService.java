package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.bot.StarkeVerbenBot;
import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.Notification;
import de.viktorlevin.starkeverbenbot.repository.NotificationRepository;
import de.viktorlevin.starkeverbenbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final StarkeVerbenBot bot;
    private final TextService textService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final StarkeVerbenService starkeVerbenService;
    private final WortService wortService;


    @Transactional(readOnly = true)
    public List<BotUser> getUsersWithoutActivity(long millsWithoutActivity) {
        return userRepository.getUsersWithoutActivityAndRecentNotification(
                OffsetDateTime.now().minus(millsWithoutActivity, ChronoUnit.MILLIS));
    }


    @Transactional
    public void sendActivityNotifications(List<BotUser> users) {
        users.stream().parallel().forEach(this::sendActivityNotification);
    }

    @Transactional
    public void sendTopNotifications(List<BotUser> users, int topsize) {
        users.forEach(user -> sendTopNotification(user, topsize));
    }

    private void sendTopNotification(BotUser user, int topSize) {
        try {
            sendAndSaveTopNotification(user, topSize);
        } catch (Exception exception) {
            log.error("Error during sending notification to user {}", user.getId(), exception);
            userService.markUserAsInactive(user);
        }
    }

    private void sendAndSaveTopNotification(BotUser user, int topSize) {
        long quantityOfVerbs = starkeVerbenService.getQuantityOfLearnedVerbs(user);
        long quantityOfWords = wortService.getQuantityOfLearnedWords(user);
        SendMessage sendMessage = textService.createTopNotification(
                user.getChatId(),
                quantityOfVerbs,
                quantityOfWords,
                topSize
        );
        sendNotificationThroughBot(sendMessage, user, Notification.Type.RATING);
    }

    private void sendActivityNotification(BotUser user) {
        try {
            sendAndSaveActiveNotification(user);
        } catch (Exception exception) {
            log.error("Error during sending notification to user {}", user.getId(), exception);
            userService.markUserAsInactive(user);
        }
    }

    private void sendAndSaveActiveNotification(BotUser user) {
        SendMessage sendMessage = textService.createActivityNotification(user.getChatId());
        sendNotificationThroughBot(sendMessage, user, Notification.Type.ACTIVITY);
    }

    @Transactional(readOnly = true)
    public List<BotUser> getTopActiveUsersForLastNHours(int topSize, int lastHours) {
        return userRepository.getTopActiveUsers(topSize);
    }

    private void sendNotificationThroughBot(SendMessage sendMessage, BotUser user, Notification.Type type) {
        bot.sendApiMethodToUser(List.of(sendMessage));
        notificationRepository.save(new Notification(user, type));
    }
}