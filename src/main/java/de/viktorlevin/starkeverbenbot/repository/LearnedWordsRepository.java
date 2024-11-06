package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedWort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearnedWordsRepository extends JpaRepository<LearnedWort, Integer> {

    long countByUserAndStatus(BotUser user, LearnedWort.Status status);

    long countByUser(BotUser user);

    Optional<LearnedWort> findByUserAndWort_Id(BotUser user, Integer wortId);

    List<LearnedWort> findAllByUserAndStatus(BotUser user, LearnedWort.Status status);
}

