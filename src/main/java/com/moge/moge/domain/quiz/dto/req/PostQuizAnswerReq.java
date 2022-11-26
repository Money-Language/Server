package com.moge.moge.domain.quiz.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PostQuizAnswerReq {
    private String hint;
    private String content;
    private int isAnswer;
    private int quizIdx;

    public String getHint() {
        return hint;
    }

    public String getContent() {
        return content;
    }

    public int getIsAnswer() {
        return isAnswer;
    }

    public int getQuizIdx() {
        return quizIdx;
    }
}
