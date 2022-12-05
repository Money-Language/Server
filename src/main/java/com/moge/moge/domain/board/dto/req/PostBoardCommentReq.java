package com.moge.moge.domain.board.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostBoardCommentReq {
    private String content; // 신고
    private int groupIdx; // 어떤 그루브이 댓글인지를 구분함
    private int parentIdx; // 0 : 부모댓글, 1 : 자녀댓글
}
