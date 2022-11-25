package com.moge.moge.domain.quiz.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostBoardRes {
    private int boardIdx;
    private int categoryIdx;
    private int userIdx;
    private String title;
}
