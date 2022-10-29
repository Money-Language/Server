package com.moge.moge.domain.board;

import com.moge.moge.domain.board.model.res.GetBoardTopRes;
import com.moge.moge.domain.board.model.req.PatchBoardCommentReq;
import com.moge.moge.domain.board.model.req.PostBoardCommentReq;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@Service
public class BoardService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BoardDao boardDao;
    private final BoardProvider boardProvider;
    private final JwtService jwtService;

    @Autowired
    public BoardService(BoardDao boardDao, BoardProvider boardProvider, JwtService jwtService) {
        this.boardDao = boardDao;
        this.boardProvider = boardProvider;
        this.jwtService = jwtService;
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

    public List<GetBoardTopRes> getBoardTopLike() throws BaseException {
        try {
            return boardDao.getBoardTopLike();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardTopRes> getBoardTopView() throws BaseException {
        try {
            return boardDao.getBoardTopView();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int createBoardComment(PostBoardCommentReq postBoardCommentReq, int boardIdx, int userIdx) throws BaseException {
        try {
            if (postBoardCommentReq.getParentIdx() == 1) { // 만약 대댓글을 작성할 때 그룹 IDX가 존재하지 않으면 대댓글을 작성할 수 없음
               if (boardDao.checkCommentGroupIdx(postBoardCommentReq.getGroupIdx()) == 0) {
                    throw new BaseException(BOARD_COMMENT_GROUP_IDX_NOT_EXISTS);
                }
            }
            return boardDao.createBoardComment(postBoardCommentReq, boardIdx, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int updateBoardComment(PatchBoardCommentReq patchBoardCommentReq, int commentIdx) throws BaseException {
        try {
            if (boardDao.updateBoardComment(patchBoardCommentReq, commentIdx) == 0) {
                throw new BaseException(FAILED_TO_UPDATE_COMMENT);
            }
            return boardDao.updateBoardComment(patchBoardCommentReq, commentIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int deleteBoardComment(int commentIdx) throws BaseException {
        try {
            if (boardDao.deleteBoardComment(commentIdx) == 0) {
                throw new BaseException(FAILED_TO_DELETE_COMMENT);
            }
            return boardDao.deleteBoardComment(commentIdx);
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

}
