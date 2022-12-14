package com.moge.moge.domain.board.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardTopRes {
    private int boardIdx;
    private String categoryName;
    private String title;
    private int viewCount;
    private int likeCount;
    private int quizCount;
    private int commentCount;
    private String nickname;
    private String profileImage;
    private String elapsedTime;
}
