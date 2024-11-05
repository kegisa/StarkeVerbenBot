package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<BotUser, Integer> {
    Optional<BotUser> findByChatId(Long chatId);

    List<BotUser> findAll();

    @Query(value = "SELECT * FROM bot_users WHERE id IN (SELECT rs.user_id FROM requests_statistic rs " +
            "        left join notification n on rs.user_id = n.user_id GROUP BY rs.user_id " +
            "         HAVING (max(rs.requested_at) < ?1 " +
            "          AND (max(n.sent_at) < ?1) OR max(n.sent_at) is null)) AND id < 3 AND bot_users.is_active = true;",
            nativeQuery = true)
    List<BotUser> getUsersWithoutActivityAndRecentNotification(OffsetDateTime now);
}

