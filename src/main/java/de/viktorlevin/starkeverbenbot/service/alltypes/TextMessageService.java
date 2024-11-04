package de.viktorlevin.starkeverbenbot.service.alltypes;

import de.viktorlevin.starkeverbenbot.entity.BotUser;
import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import de.viktorlevin.starkeverbenbot.service.*;
import de.viktorlevin.starkeverbenbot.service.telegram.KeyboadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@Service
@RequiredArgsConstructor
public class TextMessageService {
    private final UserService userService;
    private final StatisticService statisticService;
    private final WortService wortService;
    private final StarkeVerbenService starkeVerbenService;
    private final TextService textService;
    private final KeyboadService keyboadService;

    public SendMessage processTextMessage(Message message) {
        try {
            String userName = message.getChat().getUserName();
            Long chatId = message.getChat().getId();
            String messageText = message.getText();

            log.info("Got text message from {} with username {}", chatId, userName);
            BotUser user = userService.registrateUser(chatId, userName);
            statisticService.saveRequestToStatistic(user, messageText);

            log.info("Text is {}", messageText);

            switch (messageText) {
                case "/start":
                    wortService.initializeLearningProcess(user);
                    starkeVerbenService.initializeLearningProcess(user);
                    SendMessage startMessage = textService.startBot(chatId);
                    return keyboadService.addKeyBoardMarkup(startMessage);
                case "/einverb", "ein starkes Verb bitte", "/einVerb":
                    StarkesVerb starkesVerb = starkeVerbenService.getRandomStarkesVerb(user);
                    SendMessage verbMessage = textService.generateStarkesVerb(starkesVerb, chatId);
                    return keyboadService.addInlineKeyBoardForVerb(verbMessage, starkesVerb.getId());
                case "/einwort", "ein Wort bitte", "/einWort":
                    Wort wort = wortService.getRandomWort(user);
                    SendMessage wordMessage = textService.generateWort(wort, chatId);
                    return keyboadService.addInlineKeyBoardForWord(wordMessage, wort.getId());
                default:
                    throw new IllegalStateException("Я не понимаю, что мне делать...");
            }
        } catch (Exception exception) {
            log.error(exception.toString());
            return textService.createMessage(message.getChat().getId(), exception.getMessage());
        }
    }

}