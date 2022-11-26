package com.moge.moge.domain.quiz.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostQuizReq {
    private String question; //질문
    private int quizType; // 객관식 or 주관식
    private int boardIdx;

}
