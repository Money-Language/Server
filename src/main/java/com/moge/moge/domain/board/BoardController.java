package com.moge.moge.domain.board;


import com.moge.moge.domain.board.model.req.PostCommentReportReq;
import com.moge.moge.domain.board.model.res.GetBoardCommentRes;
import com.moge.moge.domain.board.model.res.GetBoardSearchRes;
import com.moge.moge.domain.board.model.res.GetBoardTopRes;
import com.moge.moge.domain.board.model.req.PatchBoardCommentReq;
import com.moge.moge.domain.board.model.req.PostBoardCommentReq;
import com.moge.moge.domain.user.UserProvider;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/boards")
public class BoardController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private final BoardProvider boardProvider;
    @Autowired private final BoardService boardService;
    @Autowired private final UserProvider userProvider;
    @Autowired private final JwtService jwtService;

    public BoardController(BoardProvider boardProvider, BoardService boardService, UserProvider userProvider, JwtService jwtService){
        this.boardProvider = boardProvider;
        this.boardService = boardService;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
    }

    /* 게시글 좋아요 등록, 취소 */
    @ResponseBody
    @PostMapping("/{boardIdx}/like")
    public BaseResponse<String> createBoardLike(@PathVariable("boardIdx") int boardIdx) {
        try {
            // jwt 토큰 확인
            int userIdxByJwt = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdxByJwt) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            boardService.createBoardLike(boardIdx, userIdxByJwt);
            return new BaseResponse<>(SUCCESS_CREATE_BOARD_LIKE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글 좋아요 top 10 조회 */
    @ResponseBody
    @GetMapping("/top-like")
    public BaseResponse<List<GetBoardTopRes>> getBoardTopLike() {
        try {
            List<GetBoardTopRes> boardTopLike = boardProvider.getBoardTopLike();
            return new BaseResponse<>(boardTopLike);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글 검색 */
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<List<GetBoardSearchRes>> getBoardByKeyword(@RequestParam("title") String title) {
        try {
            if (title == null) {
                return new BaseResponse<>(EMPTY_SEARTCH_KEYWORD);
            }
            List<GetBoardSearchRes> getBoardSearchRes = boardProvider.getBoardByKeyword(title);
            return new BaseResponse<>(getBoardSearchRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글 조회 top 10 조회 */
    @ResponseBody
    @GetMapping("/top-view")
    public BaseResponse<List<GetBoardTopRes>> getBoardTopView() {
        try {
            List<GetBoardTopRes> boardTopView = boardProvider.getBoardTopView();
            return new BaseResponse<>(boardTopView);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /* 게시글 댓글 생성 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comments")
    public BaseResponse<String> createBoardComment(@PathVariable("boardIdx") int boardIdx,
                                                  @RequestBody PostBoardCommentReq postBoardCommentReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdx) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            if (postBoardCommentReq.getContent() == null) {
                return new BaseResponse<>(POST_BOARDS_EMPTY_COMMENT);
            }
            boardService.createBoardComment(postBoardCommentReq, boardIdx, userIdx);
            return new BaseResponse<>(SUCCESS_CREATE_BOARD_COMMENT);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글 수정 */
    @ResponseBody
    @PatchMapping("/{boardIdx}/comments/{commentIdx}")
    public BaseResponse<String> updateBoardComment(@PathVariable("boardIdx") int boardIdx,
                                                   @PathVariable("commentIdx") int commentIdx,
                                                   @RequestBody PatchBoardCommentReq patchBoardCommentReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdx) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            if (patchBoardCommentReq.getContent() == null) {
                return new BaseResponse<>(POST_BOARDS_EMPTY_COMMENT);
            }
            // 유저가 작성한 댓글인지 확인하기
            if (userProvider.checkUserComment(userIdx, commentIdx, boardIdx) == 0) {
                return new BaseResponse<>(POST_BOARDS_COMMENT_INVALID_JWT); // 좀더 나은 이름으로 변경하자 : 권한이 없는 유저다!
            }
            boardService.updateBoardComment(patchBoardCommentReq, commentIdx);
            return new BaseResponse<>(SUCCESS_UPDATE_BOARD_COMMENT);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글 삭제 */
    @ResponseBody
    @DeleteMapping("/{boardIdx}/comments/{commentIdx}")
    public BaseResponse<String> deleteBoardComment(@PathVariable("boardIdx") int boardIdx,
                                                   @PathVariable("commentIdx") int commentIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdx) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            if (userProvider.checkUserComment(userIdx, commentIdx, boardIdx) == 0) {
                return new BaseResponse<>(POST_BOARDS_COMMENT_INVALID_JWT);
            }
            boardService.deleteBoardComment(boardIdx, commentIdx);
            return new BaseResponse<>(SUCCESS_DELETE_BOARD_COMMENT);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글/대댓글 좋아요 누르기 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comments/{commentIdx}/like")
    public BaseResponse<String> createCommentLike(@PathVariable("boardIdx") int boardIdx,
                                                   @PathVariable("commentIdx") int commentIdx) {
        try {
            int userIdx = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdx) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            boardService.createCommentLike(boardIdx, commentIdx, userIdx);
            return new BaseResponse<>(SUCCESS_CREATE_BOARD_COMMENT_LIKE);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글 전체 조회 */
    @ResponseBody
    @GetMapping("/{boardIdx}/comments")
    public BaseResponse<List<GetBoardCommentRes>> getBoardComments(@PathVariable("boardIdx") int boardIdx) {
        try {
            return new BaseResponse<>(boardProvider.getBoardComments(boardIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 그룹 식별자로 해당 그룹의 댓글, 대댓글을 전체 조회 */
    @ResponseBody
    @GetMapping("/{boardIdx}/comments/group")
    public BaseResponse<List<GetBoardCommentRes>> getBoardCommentsByGroup(@PathVariable("boardIdx") int boardIdx,
                                                                   @RequestParam("groupIdx") int groupIdx) {
        try {
            return new BaseResponse<>(boardProvider.getBoardCommentsByGroup(boardIdx, groupIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글 신고하기 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comments/{commentIdx}/report")
    public BaseResponse<String> reportComments(@PathVariable("boardIdx") int boardIdx,
                                               @PathVariable("commentIdx") int commentIdx,
                                               @RequestBody PostCommentReportReq postCommentReportReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdx) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            boardService.reportComment(userIdx, boardIdx, commentIdx, postCommentReportReq);
            return new BaseResponse<>(SUCCESS_CREATE_COMMENT_REPORT);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
