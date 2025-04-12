package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StarkesVerbRepository extends JpaRepository<StarkesVerb, Integer> {

    @Query(
            value = "SELECT * FROM starke_verben ORDER BY RANDOM() LIMIT ?1;",
            nativeQuery = true)
    List<StarkesVerb> getFirstNRandomVerbs(long numberVerbs);

    @Query(
            value = "SELECT * FROM starke_verben WHERE starke_verben.id NOT IN (SELECT verb_id FROM learned_starke_verben lsv " +
                    "WHERE lsv.user_id = ?1) " +
                    "ORDER BY RANDOM() LIMIT 1;",
            nativeQuery = true)
    Optional<StarkesVerb> getNewVerbForLearning(Integer userId);
}

