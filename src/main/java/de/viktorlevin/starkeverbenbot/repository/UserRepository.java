package de.viktorlevin.starkeverbenbot.repository;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<BotUser, Integer> {
    Optional<BotUser> findByChatId(Long chatId);

    List<BotUser> findAll();
}

