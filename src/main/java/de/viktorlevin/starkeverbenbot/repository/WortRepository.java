package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.Wort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WortRepository extends JpaRepository<Wort, Integer> {

    @Query(
            value = "SELECT * FROM worte ORDER BY RANDOM() LIMIT ?1;",
            nativeQuery = true)
    List<Wort> getFirstNRandomWords(long numberWords);
}

