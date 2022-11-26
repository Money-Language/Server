package com.moge.moge.domain.board.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardRes {
    private int boardIdx;
    private String nickname;
    private String profileImage;
    private String elapsedTime;
    private String title;
    private int quizCount;
    private int viewCount;
    private int likeCount;
    private int commentCount;
}
