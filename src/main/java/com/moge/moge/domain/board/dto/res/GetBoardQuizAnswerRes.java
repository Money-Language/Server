package com.moge.moge.domain.board.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardQuizAnswerRes {
    private int quizIdx;
    private String content;
    private int isAnswer;
    private String hint;
}
