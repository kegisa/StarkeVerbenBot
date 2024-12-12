package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.LearnedWort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LearnedWordsRepository extends JpaRepository<LearnedWort, Integer> {

    long countByUserAndStatus(BotUser user, LearnedWort.Status status);

    @Query(value = "select count(*) verbs from learned_words lw " +
            "where lw.user_id = ?1 and lw.status = 1 and lw.updated_at > (now() - (interval '1 hour' * ?2)) group by lw.user_id", nativeQuery = true)
    Long countByUserAndStatusForLastNHours(int user, int lastNHours);

    Long countByUser(BotUser user);

    Optional<LearnedWort> findByUserAndWort_Id(BotUser user, Integer wortId);

    @EntityGraph(attributePaths = {"wort"})
    List<LearnedWort> findAllByUserAndStatus(BotUser user, LearnedWort.Status status);

    @Query(value = "select count(*) from (select count(*), user_id from learned_words where status = 1 group by user_id having (count(*) = 1080)) sub;", nativeQuery = true)
    Long usersFinished();
}

