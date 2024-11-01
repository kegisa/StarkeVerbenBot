package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedStarkesVerb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LearnedStarkeVerbenRepository extends JpaRepository<LearnedStarkesVerb, Integer> {
    @Query(
            value = "SELECT * FROM learned_starke_verben WHERE user_id = ?1 AND status = 0 ORDER BY RANDOM() LIMIT 1;",
            nativeQuery = true)
    Optional<LearnedStarkesVerb> getRandomStarkesVerb(Integer userId);

    long countByUserAndStatus(BotUser user, LearnedStarkesVerb.Status status);

    long countByUser(BotUser user);

    Optional<LearnedStarkesVerb> findByUserAndVerb_Id(BotUser user, Integer verbId);
}

