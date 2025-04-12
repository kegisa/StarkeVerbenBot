package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

        Optional<BotUser> optUser = userRepository.findByChatId(chatId);
        BotUser botUser = null;

        if (optUser.isPresent()) {
            botUser = optUser.get();
            botUser.setActive(true);
        } else {
            botUser = userRepository.save(new BotUser(userName, chatId));
        }
        cachedUsers.put(chatId, botUser);
        return botUser;
    }

    @Transactional(readOnly = true)
    public List<BotUser> getUsers() {
        return userRepository.findAll();
    }

    public Optional<BotUser> findByChatId(Long chatId) {
        return userRepository.findByChatId(chatId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markUserAsInactive(BotUser user) {
        cachedUsers.remove(user.getChatId());
        user.setActive(false);
        userRepository.save(user);
    }
}