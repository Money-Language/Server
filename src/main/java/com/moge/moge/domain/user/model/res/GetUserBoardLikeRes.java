package com.moge.moge.domain.user.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserBoardLikeRes {
    private String categoryName;
    private String title;
    private int quizCount; // 게시글의 퀴즈 개수
    private int viewCount;
    private int likeCount;
}
