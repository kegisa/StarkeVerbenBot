package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedWort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LearnedWordsRepository extends JpaRepository<LearnedWort, Integer> {
    @Query(
            value = "SELECT * FROM learned_words WHERE user_id = ?1 AND status = 0 ORDER BY RANDOM() LIMIT 1;",
            nativeQuery = true)
    LearnedWort getRandomWort(Integer userId);

    long countByUser(BotUser user);
}

