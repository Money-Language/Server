package com.moge.moge.domain.board.service;

import com.moge.moge.domain.board.dao.BoardDao;
import com.moge.moge.domain.board.dto.req.PostBoardReportReq;
import com.moge.moge.domain.board.dto.req.PostCommentReportReq;
import com.moge.moge.domain.board.dto.req.PatchBoardCommentReq;
import com.moge.moge.domain.board.dto.req.PostBoardCommentReq;
import com.moge.moge.domain.s3.S3Service;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.util.ValidationUtils.checkCommentNull;

@Service
public class BoardService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BoardDao boardDao;
    private final BoardProvider boardProvider;
    private final JwtService jwtService;
    private final S3Service s3Service;

    @Autowired
    public BoardService(BoardDao boardDao, BoardProvider boardProvider, JwtService jwtService, S3Service s3Service) {
        this.boardDao = boardDao;
        this.boardProvider = boardProvider;
        this.jwtService = jwtService;
        this.s3Service = s3Service;
    }

    public void createBoardLike(int boardIdx, int userIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }

            // 좋아요가 눌려있으면
            if (boardDao.checkBoardLikeExists(boardIdx, userIdx) == 1) {
                boardDao.deleteBoardLike(boardIdx, userIdx);
            } else { // 안눌려있으면
                if (boardDao.checkBoardLikeStatus(boardIdx, userIdx) == 1) {
                    boardDao.updateBoardLikeStatus(boardIdx, userIdx);
                } else {
                    int result = boardDao.createBoardLike(boardIdx, userIdx);
                    if (result == 0) {
                        throw new BaseException(FAILED_TO_CREATE_BOARD_LIKE);
                    }
                }
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int createBoardComment(PostBoardCommentReq postBoardCommentReq, int boardIdx, int userIdx) throws BaseException {
        if (postBoardCommentReq.getParentIdx() == 1) { // 만약 대댓글을 작성할 때 그룹 IDX가 존재하지 않으면 대댓글을 작성할 수 없음
            if (boardDao.checkCommentGroupIdx(postBoardCommentReq.getGroupIdx()) == 0) {
                throw new BaseException(BOARD_COMMENT_GROUP_IDX_NOT_EXISTS);
            }
        }

        if (postBoardCommentReq.getParentIdx() == 0) {
            if (boardDao.checkGroupParentIdx(postBoardCommentReq, boardIdx) == 1) {
                throw new BaseException(BOARD_COMMENT_GROUP_PARENT_IDX_EXISTS);
            }
        }

        try {
            return boardDao.createBoardComment(postBoardCommentReq, boardIdx, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updateBoardComment(PatchBoardCommentReq patchBoardCommentReq, int commentIdx) throws BaseException {
        if (boardDao.updateBoardComment(patchBoardCommentReq, commentIdx) == 0) {
            throw new BaseException(FAILED_TO_UPDATE_COMMENT);
        }
        try {
            checkCommentNull(patchBoardCommentReq.getContent());
            return boardDao.updateBoardComment(patchBoardCommentReq, commentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteBoardComment(int boardIdx, int commentIdx) throws BaseException {
        try {
            // 댓글 식별자로 해당 댓글의 부모식별자를 알아옴 -> 만약 1이라면 그냥 삭제, 0이면 댓글수정
            if (boardDao.checkCommentParentIdx(commentIdx) == 1) {
                boardDao.deleteChildComment(boardIdx, commentIdx);
            } else {
                boardDao.deleteParentComment(boardIdx, commentIdx);
            }

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createCommentLike(int boardIdx, int commentIdx, int userIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            if (boardDao.checkCommentLikeExists(commentIdx, userIdx) == 1) {
                boardDao.deleteCommentLike(commentIdx, userIdx);
            } else {
                if (boardDao.checkCommentLikeStatus(commentIdx, userIdx) == 1) {
                    boardDao.updateCommentLikeStatus(commentIdx, userIdx);
                } else {
                    int result = boardDao.createCommentLike(boardIdx, commentIdx, userIdx);
                    if (result == 0) {
                        throw new BaseException(FAILED_TO_CREATE_COMMENT_LIKE);
                    }
                }
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void reportComment(int userIdx, int boardIdx, int commentIdx, PostCommentReportReq postCommentReportReq) throws BaseException {
        // 해당 댓글이 존재하지 않으면 에러 발생
        if (boardDao.checkCommentExists(commentIdx) == 0) {
            throw new BaseException(COMMENT_NOT_EXISTS);
        }
        if (postCommentReportReq.getContent() == null) {
            throw new BaseException(EMPTY_COMMENTS_REPORT_CONTENT);
        }
        // 자기가 쓴 댓글은 신고 불가능
        if (boardDao.checkCommentWriter(commentIdx) == userIdx) {
            throw new BaseException(FAILED_TO_CREATE_COMMENT_REPORT);
        }
        // 해당 유저가 이미 신고한 댓글이면 신고 불가능
        if (boardDao.checkCommentReportAlreadyExists(userIdx, commentIdx) == 1) {
            throw new BaseException(COMMENT_REPORT_ALREADY_EXISTS);
        }
        // 신고누적횟수가 3회 이상이면 신고 불가능
        if (boardDao.checkCommentReportCount(commentIdx) >= 3) {
            boardDao.updateCommentStatus(commentIdx); // 신고횟수가 3번 쌓이면 INACTIVE로 status를 바꾸어줌
            throw new BaseException(COMMENT_REPORT_EXCEED);
        }

        try {
            boardDao.reportComment(userIdx, boardIdx, commentIdx, postCommentReportReq);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void reportBoard(int userIdx, int boardIdx, PostBoardReportReq postBoardReportReq) throws BaseException {
        //try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            if (postBoardReportReq.getContent() == null) {
                throw new BaseException(EMPTY_BOARDS_REPORT_CONTENT);
            }
            if (boardDao.checkBoardWriter(boardIdx) == userIdx) {
                throw new BaseException(FAILED_TO_CREATE_BOARD_REPORT);
            }
            if (boardDao.checkBoardReportAlreadyExists(userIdx, boardIdx) == 1) {
                throw new BaseException(BOARD_REPORT_ALREADY_EXISTS);
            }
            if (boardDao.checkBoardReportCount(boardIdx) >= 3) {
                boardDao.updateBoardStatus(boardIdx);
                throw new BaseException(BOARD_REPORT_EXCEED);
            }
            boardDao.reportBoard(userIdx,boardIdx, postBoardReportReq);
        //} catch (Exception exception) {
        //    throw new BaseException(DATABASE_ERROR);
        //}
    }

    public void updateViewCount(int boardIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            int result = boardDao.updateViewCount(boardIdx);
            if (result == 0) {
                throw new BaseException(FAILED_TO_UPDATE_VIEW_COUNT);
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
