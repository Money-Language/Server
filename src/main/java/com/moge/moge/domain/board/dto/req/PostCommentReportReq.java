package com.moge.moge.domain.board.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentReportReq {
    private String content; //신고사유
}
