package com.moge.moge.domain.board;

import com.moge.moge.domain.board.model.GetBoardTop;
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
    public BaseResponse<List<GetBoardTop>> getBoardTopLike() {
        try {
            List<GetBoardTop> boardTopLike = boardService.getBoardTopLike();
            return new BaseResponse<>(boardTopLike);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글 조회 top 10 조회 */
    @ResponseBody
    @GetMapping("/top-view")
    public BaseResponse<List<GetBoardTop>> getBoardTopView() {
        try {
            List<GetBoardTop> boardTopView = boardService.getBoardTopView();
            return new BaseResponse<>(boardTopView);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    /* 게시글 댓글 생성 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comment")
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
    @PatchMapping("/{boardIdx}/comment/{commentIdx}")
    public BaseResponse<String> updateBoardComment(@PathVariable("boardIdx") int boardIdx,
                                                   @PathVariable("commentIdx") int commentIdx,
                                                   @RequestBody PatchBoardCommentReq patchBoardCommentReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            if (userProvider.checkUser(userIdx) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
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
    @DeleteMapping("/{boardIdx}/comment/{commentIdx}")
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
            boardService.deleteBoardComment(commentIdx);
            return new BaseResponse<>(SUCCESS_DELETE_BOARD_COMMENT);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글/대댓글 좋아요 누르기 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comment/{commentIdx}/like")
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

}
