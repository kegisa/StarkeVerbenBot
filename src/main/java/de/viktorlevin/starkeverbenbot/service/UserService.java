package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private static final Map<Long, String> cachedUsers = new HashMap<>();

    @Transactional
    public void registrateUser(Long chatId, String userName) {
        if (cachedUsers.containsKey(chatId)) {
            return;
        }

        userRepository.findByChatId(chatId).ifPresentOrElse(
                user -> cachedUsers.put(chatId, userName),
                () -> userRepository.save(new BotUser(userName, chatId)));
    }

    @Transactional(readOnly = true)
    public List<BotUser> getUsers() {
        return userRepository.findAll();
    }
}