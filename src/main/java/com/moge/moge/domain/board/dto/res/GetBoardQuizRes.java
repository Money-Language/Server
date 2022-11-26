package com.moge.moge.domain.board.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardQuizRes {
    private int quizIdx;
    private int quizType;
    private String question;
}
