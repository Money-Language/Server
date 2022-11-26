package com.moge.moge.domain.board.model.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetBoardCommentRes {
    private int commentIdx;
    private int groupIdx;
    private String content;
    private int parentIdx;
    private String elapsedTime;
    private String nickname;
    private String profileImage;
    private int commentCount;
    private int commentLike;
}
