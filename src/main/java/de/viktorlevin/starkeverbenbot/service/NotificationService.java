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


    @Transactional(readOnly = true)
    public List<BotUser> getUsersWithoutActivity(long millsWithoutActivity) {
        return userRepository.getUsersWithoutActivityAndRecentNotification(
                OffsetDateTime.now().minus(millsWithoutActivity, ChronoUnit.MILLIS));
    }


    @Transactional
    public void sendNotifications(List<BotUser> users) {
        users.stream().forEach(this::sendNotification);
    }

    private void sendNotification(BotUser user) {
        try {
            sendAndSaveNotification(user);
        } catch (Exception exception) {
            log.error("Error during sending notification to user {}", user.getId(), exception);
            userService.markUserAsInactive(user);
        }
    }
    private void sendAndSaveNotification(BotUser user) {
        SendMessage sendMessage = textService.createActivityNotification(user.getChatId());
        bot.sendApiMethodToUser(List.of(sendMessage));
        notificationRepository.save(new Notification(user, Notification.Type.ACTIVITY));
    }


}