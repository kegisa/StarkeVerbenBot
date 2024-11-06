package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedStarkesVerb;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearnedStarkeVerbenRepository extends JpaRepository<LearnedStarkesVerb, Integer> {

    long countByUserAndStatus(BotUser user, LearnedStarkesVerb.Status status);

    long countByUser(BotUser user);

    Optional<LearnedStarkesVerb> findByUserAndVerb_Id(BotUser user, Integer verbId);

    List<LearnedStarkesVerb> findAllByUserAndStatus(BotUser user, LearnedStarkesVerb.Status status);
}

