package com.moge.moge.domain.quiz.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostQuizRes {
    private String question;
    private int quizType;
    private int boardIdx;
    private int quizIdx;
}
