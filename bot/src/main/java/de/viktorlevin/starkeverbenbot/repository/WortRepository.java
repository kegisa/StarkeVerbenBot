package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.Wort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WortRepository extends JpaRepository<Wort, Integer> {

    @Query(
            value = "SELECT * FROM worte ORDER BY RANDOM() LIMIT ?1;",
            nativeQuery = true)
    List<Wort> getFirstNRandomWords(long numberWords);

    @Query(
            value = "SELECT * FROM worte WHERE worte.id NOT IN (SELECT word_id FROM learned_words lw " +
                    "WHERE lw.user_id = ?1) " +
                    "ORDER BY RANDOM() LIMIT 1;",
            nativeQuery = true)
    Optional<Wort> getNewWordForLearning(Integer userId);
}

