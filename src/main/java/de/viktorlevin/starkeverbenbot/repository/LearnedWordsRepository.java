package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedWort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LearnedWordsRepository extends JpaRepository<LearnedWort, Integer> {
    @Query(
            value = "SELECT * FROM learned_words WHERE user_id = ?1 AND status = 0 ORDER BY RANDOM() LIMIT 1;",
            nativeQuery = true)
    Optional<LearnedWort> getRandomWort(Integer userId);

    long countByUserAndStatus(BotUser user, LearnedWort.Status status);

    long countByUser(BotUser user);

    Optional<LearnedWort> findByUserAndWort_Id(BotUser user, Integer wortId);

}

