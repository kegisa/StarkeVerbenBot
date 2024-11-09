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
            "          AND (max(n.sent_at) < ?1 OR max(n.sent_at) is null))) AND bot_users.is_active = true;",
            nativeQuery = true)
    List<BotUser> getUsersWithoutActivityAndRecentNotification(OffsetDateTime now);

    @Query(value = "select bu.* from bot_users bu " +
            "left join (select lw.user_id, count(*) words from learned_words lw where lw.status = 1 group by lw.user_id) as words on bu.id = words.user_id " +
            "left join (select lv.user_id, count(*) verbs from learned_starke_verben lv where lv.status = 1 group by lv.user_id) as verbs on bu.id = verbs.user_id " +
            "where bu.is_active = true " +
            "order by COALESCE(words.words, 0) + COALESCE(verbs.verbs, 0) desc limit ?1;", nativeQuery = true)
    List<BotUser> getTopActiveUsers(int topSize);
}

