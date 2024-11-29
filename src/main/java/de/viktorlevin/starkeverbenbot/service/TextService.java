package de.viktorlevin.starkeverbenbot.service;

import de.viktorlevin.starkeverbenbot.entity.StarkesVerb;
import de.viktorlevin.starkeverbenbot.entity.UserStatistic;
import de.viktorlevin.starkeverbenbot.entity.Wort;
import de.viktorlevin.starkeverbenbot.service.telegram.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TextService {

    private final MessageService messageService;
    private static final Random random = new Random();
    private final String BEKOMMEN_HELP = """
            Бот поможет тебе выучить 1080 самых популярных немецких слов и 172 сильных глагола.
            Жмите кнопку “Убрать слово! Я запомнил” только когда уверены, что запомнили слово.
                        
            Кнопка "ein Wort bitte" --> Получить рандомное слово с переводом
            Кнопка "ein starkes Verb bitte" --> Получить рандомный сильный глагол с переводом и формами
                        
            Также можно воспользоваться ссыклами:
            /einverb
            /einwort
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
    private static final String MESSAGE_FOR_GROUP = """
            Всем участникам группы привет!
                        
            Бот поможет вам выучить 1080 самых популярных немецких слов и 172 сильных глагола.
            Жмите кнопку “Убрать слово! Я запомнил” только когда уверены, что запомнили слово.
                        
            Важно, что выученные слова будут сохраняться для всей группы сразу, так как бот запущен в группе. Чтобы иметь свой собственный процесс пользуйтесь ботом лично @StarkeVerbenDeutschBot. 
                        
            Связь с разработчиком - @kegisa
                        
            Кнопка "ein Wort bitte" --> Получить рандомное слово с переводом
            Кнопка "ein starkes Verb bitte" --> Получить рандомный сильный глагол с переводом и формами
                        
            Также можно воспользоваться ссыклами:
            /einverb
            /einwort
            """;

    private static final String NOTIFICATION_INACTIVITY = """
            %s
                        
            Помнишь перевод этого слова? 🧐
            Не забывай про регулярность — 5 минут в день помогут тебе быстрее выучить новые слова.  📚 Успех близко! 💪
            """;
    private static final String TOP_RATING_MESSAGE = """
            🎉 Вы входите в топ %d пользователей бота по выученным словам и глаголам!
                        
            📚 Всего слов выучено: %d из 1080.
            💪 Всего сильных глаголов выучено: %d из 172.
            🚀 Не останавливайтесь на достигнутом! Вы на правильном пути к совершенству! 🌟
                        
            Данную статистику всегда можно получить по команде /statistic. 
            """;

    private static final String STATISTIC_MESSAGE = """         
            📚 Всего слов выучено: %d из 1080.
            💪 Всего сильных глаголов выучено: %d из 172.
            🤖 Запросов в бот всего %d
            🚀 Не останавливайтесь на достигнутом! Вы на правильном пути к совершенству!
            """;

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

    public SendMessage markedVerbAsLearned(Long chatId) {
        return messageService.createMessage(chatId, "Хорошо, уберу этот глагол...И добавлю новый...");
    }

    public BotApiMethod createMessageForGroup(Long chatId) {
        return messageService.createMessage(chatId, MESSAGE_FOR_GROUP);
    }

    public SendMessage createActivityNotification(Long chatId, Wort word) {
        String wordText = WORT_AND_TRANSLATION.formatted(word.getWord(), word.getTranslation());
        String textResponse = NOTIFICATION_INACTIVITY.formatted(wordText);
        int delimeter = textResponse.indexOf('\n');

        return messageService.createMessageWithEntites(chatId, textResponse, List.of(MessageEntity.builder()
                        .type("bold")
                        .offset(0)
                        .length(delimeter)
                        .build(),
                MessageEntity.builder()
                        .type("spoiler")
                        .offset(delimeter + 1)
                        .length(word.getTranslation().length())
                        .build()));
    }

    public SendMessage createTopNotification(Long chatId, long quantityOfVerbs, long quantityOfWords, int topSize) {
        return createMessage(chatId, TOP_RATING_MESSAGE.formatted(topSize, quantityOfWords, quantityOfVerbs));
    }

    public SendMessage statisticMessage(UserStatistic statistic) {
        return createMessage(statistic.getChatId(),
                STATISTIC_MESSAGE.formatted(statistic.getLearnedWords(),
                        statistic.getLearnedStarkesVerbs(),
                        statistic.getRequests()));
    }
}

