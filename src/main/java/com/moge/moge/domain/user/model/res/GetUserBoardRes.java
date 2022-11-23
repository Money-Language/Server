package com.moge.moge.domain.user.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserBoardRes {
    private int boardIdx;
    private String categoryName;
    private String title;
    private int quizCount;
    private int viewCount;
    private int likeCount;
}
