package com.moge.moge.domain.quiz.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetDailyQuizRes {
    private int quizIdx;
    private int quizType;
    private String question;
    private int boardIdx;
}
