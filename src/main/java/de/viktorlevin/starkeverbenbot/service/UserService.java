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
    private static final Map<Long, BotUser> cachedUsers = new HashMap<>();

    @Transactional
    public BotUser registrateUser(Long chatId, String userName) {
        if (cachedUsers.containsKey(chatId)) {
            return cachedUsers.get(chatId);
        }

        BotUser user = userRepository.findByChatId(chatId)
                .orElseGet(() -> userRepository.save(new BotUser(userName, chatId)));
        cachedUsers.put(chatId, user);
        return user;
    }

    @Transactional(readOnly = true)
    public List<BotUser> getUsers() {
        return userRepository.findAll();
    }
}