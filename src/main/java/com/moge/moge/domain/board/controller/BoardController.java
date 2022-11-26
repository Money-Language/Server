package com.moge.moge.domain.board.controller;

import com.moge.moge.domain.board.dto.res.GetBoardQuizAnswerRes;
import com.moge.moge.domain.board.dto.res.GetBoardQuizRes;
import com.moge.moge.domain.board.service.BoardProvider;
import com.moge.moge.domain.board.service.BoardService;
import com.moge.moge.domain.board.model.req.PostCommentReportReq;
import com.moge.moge.domain.board.model.res.GetBoardCommentRes;
import com.moge.moge.domain.board.model.res.GetBoardSearchRes;
import com.moge.moge.domain.board.model.res.GetBoardTopRes;
import com.moge.moge.domain.board.model.req.PatchBoardCommentReq;
import com.moge.moge.domain.board.model.req.PostBoardCommentReq;
import com.moge.moge.domain.board.model.res.GetRecommendKeywordRes;
import com.moge.moge.domain.user.service.UserProvider;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import com.moge.moge.global.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.util.Constants.RECOMMEND_KEYWORD_SIZE;
import static com.moge.moge.global.util.ValidationUtils.checkCommentNull;
import static com.moge.moge.global.util.ValidationUtils.checkTitleNull;

@RestController
@RequestMapping("/app/boards")
public class BoardController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private final BoardProvider boardProvider;
    @Autowired private final BoardService boardService;
    @Autowired private final UserProvider userProvider;
    @Autowired private final JwtService jwtService;
    @Autowired private final ValidationUtils validationUtils;

    public BoardController(BoardProvider boardProvider, BoardService boardService, UserProvider userProvider, JwtService jwtService, ValidationUtils validationUtils){
        this.boardProvider = boardProvider;
        this.boardService = boardService;
        this.userProvider = userProvider;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
    }

    /* 게시글 조회수 증가 */
    @ResponseBody
    @PostMapping("/{boardIdx}/view-count")
    public BaseResponse<String> updateViewCount(@PathVariable("boardIdx") int boardIdx) {
        try {
            validationUtils.checkJwtTokenExists();
            boardService.updateViewCount(boardIdx);
            return new BaseResponse<>(SUCCESS_UPDATE_BOARD_VIEW_COUNT);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글별 퀴즈 조회*/
    @ResponseBody
    @GetMapping("/{boardIdx}/quiz")
    public BaseResponse<List<GetBoardQuizRes>> getBoardQuiz(@PathVariable("boardIdx") int boardIdx) {
        try {
            return new BaseResponse<>(boardProvider.getBoardQuiz(boardIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글별 퀴즈 정답 조회*/
    @ResponseBody
    @GetMapping("/{boardIdx}/quiz/{quizIdx}")
    public BaseResponse<List<GetBoardQuizAnswerRes>> getBoardQuizAnswers(@PathVariable("boardIdx") int boardIdx,
                                                                         @PathVariable("quizIdx") int quizIdx) {
        try {
            return new BaseResponse<>(boardProvider.getBoardQuizAnswers(boardIdx, quizIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 게시글 좋아요 등록, 취소 */
    @ResponseBody
    @PostMapping("/{boardIdx}/like")
    public BaseResponse<String> createBoardLike(@PathVariable("boardIdx") int boardIdx) {
        try {
            int userIdxByJwt = validationUtils.checkJwtTokenExists();
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
            return new BaseResponse<>(boardProvider.getBoardTopLike());
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

    /* 게시글 검색 */
    @ResponseBody
    @GetMapping("/search")
    public BaseResponse<List<GetBoardSearchRes>> getBoardByKeyword(@RequestParam("title") String title) {
        try {
            checkTitleNull(title);
            return new BaseResponse<>(boardProvider.getBoardByKeyword(title));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글 생성 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comments")
    public BaseResponse<String> createBoardComment(@PathVariable("boardIdx") int boardIdx,
                                                  @RequestBody PostBoardCommentReq postBoardCommentReq) {
        try {
            int userIdxByJwt = validationUtils.checkJwtTokenExists();
            checkCommentNull(postBoardCommentReq.getContent());
            boardService.createBoardComment(postBoardCommentReq, boardIdx, userIdxByJwt);
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
            int userIdxByJwt = validationUtils.checkJwtTokenExists();
            if (userProvider.checkUserComment(userIdxByJwt, commentIdx, boardIdx) == 0) {
                return new BaseResponse<>(POST_BOARDS_COMMENT_INVALID_JWT);
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
            int userIdxByJwt = validationUtils.checkJwtTokenExists();
            if (userProvider.checkUserComment(userIdxByJwt, commentIdx, boardIdx) == 0) {
                return new BaseResponse<>(POST_BOARDS_COMMENT_INVALID_JWT);
            }
            boardService.deleteBoardComment(boardIdx, commentIdx);
            return new BaseResponse<>(SUCCESS_DELETE_BOARD_COMMENT);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 댓글, 대댓글 좋아요 누르기 */
    @ResponseBody
    @PostMapping("/{boardIdx}/comments/{commentIdx}/like")
    public BaseResponse<String> createCommentLike(@PathVariable("boardIdx") int boardIdx,
                                                   @PathVariable("commentIdx") int commentIdx) {
        try {
            int userIdxByJwt = validationUtils.checkJwtTokenExists();
            boardService.createCommentLike(boardIdx, commentIdx, userIdxByJwt);
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
            int userIdxByJwt = validationUtils.checkJwtTokenExists();
            boardService.reportComment(userIdxByJwt, boardIdx, commentIdx, postCommentReportReq);
            return new BaseResponse<>(SUCCESS_CREATE_COMMENT_REPORT);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 추천 키워드 조회 */
    @ResponseBody
    @GetMapping("/search-keyword")
    public BaseResponse<List<GetRecommendKeywordRes>> getRecommendKeyword() {
        try {
            int keywordCounts = boardProvider.getAllRecommendKeywordCounts();

            List<Integer> randomIdxList = new ArrayList<>();
            while(randomIdxList.size() < RECOMMEND_KEYWORD_SIZE) {
                int randomNumber = (int)(Math.random() * keywordCounts + 1);
                if (!randomIdxList.contains(randomNumber)) {
                    randomIdxList.add(randomNumber);
                } else {
                    continue;
                }
            }
            return new BaseResponse<>(boardProvider.getRecommendKeyword(randomIdxList));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
