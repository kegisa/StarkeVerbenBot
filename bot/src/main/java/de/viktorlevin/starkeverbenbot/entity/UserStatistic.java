package de.viktorlevin.starkeverbenbot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistic {
    private Long chatId;
    private Long learnedWords;
    private Long learnedStarkesVerbs;
    private Long requests;
}