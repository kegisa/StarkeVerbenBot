package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import de.viktorlevin.starkeverbenbot.service.telegram.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TextService {
    private final MessageService messageService;
    private static final Random random = new Random();
    private static final String BEKOMMEN_HELP = """
            Бот поможет тебе выучить 1000 самых популярных немецких слов и 120 сильных глаголов.
            Жмите кнопку “Убрать слово! Я запомнил” только когда уверены, что запомнили слово.
            
            /einVerb --> Получить рандомный сильный глагол с переводом и формами
            /einWort --> Получить рандомное слово с переводом
            """;
    private static final String UNEXPECTED_MESSAGE = "Не могу обработать это сообщение...entschuldigung";
    private static final String INFINITIV = "%s\n";
    private static final String TWO_FORMS_AND_TRANSLATION = """
            %s  %s
            präsens - %s
            %s
            """;
    private static final String STARKES_VERB_MESSAGE = INFINITIV + TWO_FORMS_AND_TRANSLATION;
    private static final String WORT_AND_TRANSLATION = "%s\n%s";

    public SendMessage startBot(Long chatId) {
        return messageService.createMessage(chatId, BEKOMMEN_HELP);
    }


    public SendMessage unexpectedMessage(Long chatId) {
        return messageService.createMessage(chatId, UNEXPECTED_MESSAGE);
    }

    public SendMessage createMessage(Long chatId, String message) {
        return messageService.createMessage(chatId, message);
    }

    public SendMessage generateStarkesVerb(StarkesVerb starkesVerb, Long chatId) {
        String infinitiv = INFINITIV.formatted(starkesVerb.getInfinitiv());
        String textResponse = STARKES_VERB_MESSAGE.formatted(
                starkesVerb.getInfinitiv(),
                starkesVerb.getPräteritum(),
                starkesVerb.getPartizip2(),
                starkesVerb.getPräsens(),
                starkesVerb.getTranslation());

        return messageService.createMessageWithEntites(chatId, textResponse, List.of(MessageEntity.builder()
                        .type("bold")
                        .offset(0)
                        .length(starkesVerb.getInfinitiv().length())
                        .build(),
                MessageEntity.builder()
                        .type("spoiler")
                        .offset(infinitiv.length())
                        .length(textResponse.length() - infinitiv.length())
                        .build()));
    }

    public SendMessage generateWort(Wort wort, Long chatId) {
        String deutschWort = wort.getWord();
        String translation = wort.getTranslation();
        String textResponse = getRandomCombination(deutschWort, translation);
        int delimeter = textResponse.indexOf('\n');

        return messageService.createMessageWithEntites(chatId, textResponse, List.of(MessageEntity.builder()
                        .type("bold")
                        .offset(0)
                        .length(delimeter)
                        .build(),
                MessageEntity.builder()
                        .type("spoiler")
                        .offset(delimeter + 1)
                        .length(textResponse.length() - (delimeter + 1))
                        .build()));
    }

    private String getRandomCombination(String deutschWort, String translation) {
        if (random.nextBoolean()) {
            return WORT_AND_TRANSLATION.formatted(deutschWort, translation);
        } else {
            return WORT_AND_TRANSLATION.formatted(translation, deutschWort);
        }
    }

    public SendMessage markedWordAsLearned(Long chatId) {
        return messageService.createMessage(chatId, "Хорошо, уберу это слово...И добавлю новое...");
    }
}
