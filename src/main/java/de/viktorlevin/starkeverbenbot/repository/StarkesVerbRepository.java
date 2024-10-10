package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StarkesVerbRepository extends JpaRepository<StarkesVerb, Integer> {
    @Query(
            value = "SELECT * FROM starke_verben ORDER BY RANDOM() LIMIT 1;",
            nativeQuery = true)
    StarkesVerb getRandomStarkesVerb();
}

