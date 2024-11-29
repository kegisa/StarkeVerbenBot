package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedStarkesVerb;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LearnedStarkeVerbenRepository extends JpaRepository<LearnedStarkesVerb, Integer> {

    long countByUserAndStatus(BotUser user, LearnedStarkesVerb.Status status);


    @Query(value = "select count(*) verbs from learned_starke_verben lv " +
            "where lv.user_id = ?1 and lv.status = 1 and lv.updated_at > (now() - (interval '1 hour' * ?2)) group by lv.user_id", nativeQuery = true)
    Long countByUserAndStatusForLastNHours(int user, int lastNHours);

    Long countByUser(BotUser user);

    Optional<LearnedStarkesVerb> findByUserAndVerb_Id(BotUser user, Integer verbId);

    @EntityGraph(attributePaths = {"verb"})
    List<LearnedStarkesVerb> findAllByUserAndStatus(BotUser user, LearnedStarkesVerb.Status status);
}

