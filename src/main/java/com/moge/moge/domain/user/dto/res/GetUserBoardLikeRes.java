package com.moge.moge.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserBoardLikeRes {
    private int boardIdx;
    private String categoryName;
    private String title;
    private int quizCount;
    private int viewCount;
    private int likeCount;
}
