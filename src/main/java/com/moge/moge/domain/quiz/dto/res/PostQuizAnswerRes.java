package com.moge.moge.domain.quiz.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostQuizAnswerRes {
    private String content;
    private int isAnswer;
    private int quizIdx;
}
