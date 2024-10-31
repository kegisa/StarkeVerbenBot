package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;
import java.util.Random;

@Service
public class TextService {
    private final Random random = new Random();
    private static final String BEKOMMEN_HELP = """
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
        return createMessage(chatId, BEKOMMEN_HELP);
    }


    public SendMessage unexpectedMessage(Long chatId) {
        return createMessage(chatId, UNEXPECTED_MESSAGE);
    }

    private SendMessage createMessage(Long chatId, String text) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();
    }

    public SendMessage generateStarkesVerb(StarkesVerb starkesVerb, Long chatId) {
        String infinitiv = INFINITIV.formatted(starkesVerb.getInfinitiv());
        String textResponse = STARKES_VERB_MESSAGE.formatted(
                starkesVerb.getInfinitiv(),
                starkesVerb.getPräteritum(),
                starkesVerb.getPartizip2(),
                starkesVerb.getPräsens(),
                starkesVerb.getTranslation());

        return SendMessage.builder()
                .chatId(chatId)
                .text(textResponse)
                .entities(List.of(MessageEntity.builder()
                                .type("bold")
                                .offset(0)
                                .length(starkesVerb.getInfinitiv().length())
                                .build(),
                        MessageEntity.builder()
                                .type("spoiler")
                                .offset(infinitiv.length())
                                .length(textResponse.length() - infinitiv.length())
                                .build()))
                .build();
    }

    public SendMessage generateWort(Wort wort, Long chatId) {
        String deutschWort = wort.getWord();
        String translation = wort.getTranslation();
        String textResponse = getRandomCombination(deutschWort, translation);
        int delimeter = textResponse.indexOf('\n');

        return SendMessage.builder()
                .chatId(chatId)
                .text(textResponse)
                .entities(List.of(MessageEntity.builder()
                                .type("bold")
                                .offset(0)
                                .length(delimeter)
                                .build(),
                        MessageEntity.builder()
                                .type("spoiler")
                                .offset(delimeter + 1)
                                .length(textResponse.length() - (delimeter + 1))
                                .build()))
                .build();
    }

    private String getRandomCombination(String deutschWort, String translation) {
        if (random.nextBoolean()) {
            return WORT_AND_TRANSLATION.formatted(deutschWort, translation);
        } else {
            return WORT_AND_TRANSLATION.formatted(translation, deutschWort);
        }
    }

}
