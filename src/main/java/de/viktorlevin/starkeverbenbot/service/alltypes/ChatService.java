package de.viktorlevin.starkeverbenbot.service.alltypes;

import de.viktorlevin.starkeverbenbot.configuration.BotConfig;
import de.viktorlevin.starkeverbenbot.service.TextService;
import de.viktorlevin.starkeverbenbot.service.UserService;
import de.viktorlevin.starkeverbenbot.service.telegram.KeyboadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final TextService textService;
    private final KeyboadService keyboadService;
    private final UserService userService;
    private final BotConfig botConfig;

    public List<BotApiMethod> processChatMemberMessage(ChatMemberUpdated myChatMember) {
        if (isNewStatus(myChatMember, "member")) {
            log.info("Our bot was added to chat {}", myChatMember.getChat().getId());
            return List.of(keyboadService.addKeyBoardMarkup(
                    (SendMessage) textService.createMessageForGroup(myChatMember.getChat().getId())));
        } else if (isNewStatus(myChatMember, "administrator") && isOurBot(myChatMember.getNewChatMember())) {
            log.info("Our bot was promoted to admin");
            return List.of();
        } else if (isNewStatus(myChatMember, "left") && isOurBot(myChatMember.getNewChatMember())) {
            log.error("Our bot was kicked from chat {}", myChatMember.getChat().getId());
            return List.of();
        } else {
            throw new RuntimeException("Какие то действия в чате, которые я не могу обработать...");
        }
    }

    private boolean isNewStatus(ChatMemberUpdated myChatMember, String status) {
        return myChatMember.getNewChatMember().getStatus().equals(status);
    }

    public void oneMemberLeft(Message message) {
        if (botConfig.getBotName().equals(message.getLeftChatMember().getUserName())) {
            return;
        }

        User user = message.getLeftChatMember();

        log.info("Member with id {}, userName {}, name {}, lastName {} left", user.getId(), user.getUserName(), user.getFirstName(), user.getLastName());
        userService.findByChatId(user.getId()).ifPresentOrElse(
                botUser -> log.info("User with id {} is using Bot separately", user.getId()),
                () -> log.info("User with id {} is NOT using Bot separately", user.getId()));
    }

    private boolean isOurBot(ChatMember chatMember) {
        return botConfig.getBotName().equals(chatMember.getUser().getUserName());
    }
}