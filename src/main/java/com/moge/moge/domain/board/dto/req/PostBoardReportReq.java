package com.moge.moge.domain.board.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostBoardReportReq {
    private String content;
    private String boardIdx;
}
