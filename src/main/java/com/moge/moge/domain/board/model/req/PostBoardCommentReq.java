package com.moge.moge.domain.board.model.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostBoardCommentReq {
    private String content; // 댓글 내용
    private int groupIdx; // 어떤 그루브이 댓글인지를 구분함
    private int parentIdx; // 0 : 부모댓글, 1 : 자녀댓글
}
