package com.moge.moge.domain.board.service;

import com.moge.moge.domain.board.dao.BoardDao;
import com.moge.moge.domain.board.dto.res.GetBoardQuizAnswerRes;
import com.moge.moge.domain.board.dto.res.GetBoardQuizRes;
import com.moge.moge.domain.board.dto.res.GetBoardRes;
import com.moge.moge.domain.board.model.res.GetBoardCommentRes;
import com.moge.moge.domain.board.model.res.GetBoardSearchRes;
import com.moge.moge.domain.board.model.res.GetBoardTopRes;
import com.moge.moge.domain.board.model.res.GetRecommendKeywordRes;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.BOARD_NOT_EXISTS;
import static com.moge.moge.global.exception.BaseResponseStatus.DATABASE_ERROR;
import static com.moge.moge.global.util.Constants.ORDER_BY_LIKE;
import static com.moge.moge.global.util.Constants.ORDER_BY_VIEW;
import static com.moge.moge.global.util.ValidationUtils.checkCategoryIdxRange;

@Service
public class BoardProvider {

    private final BoardDao boardDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BoardProvider(BoardDao boardDao, JwtService jwtService) {
        this.boardDao = boardDao;
        this.jwtService = jwtService;
    }

    public List<GetBoardCommentRes> getBoardComments(int boardIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            return boardDao.getBoardComments(boardIdx);

        } catch (BaseException exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardCommentRes> getBoardCommentsByGroup(int boardIdx, int groupIdx) throws BaseException {
        //try {
            return boardDao.getBoardCommentsByGroup(boardIdx, groupIdx);
        //} catch (BaseException exception) {
        //    throw new BaseException(DATABASE_ERROR);
        //}
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

    public List<GetBoardSearchRes> getBoardByKeyword(String title) throws BaseException {
        try {
            return boardDao.getBoardByKeyword(title);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int getAllRecommendKeywordCounts() throws BaseException {
        try {
            return boardDao.getAllRecommendKeywordCounts();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetRecommendKeywordRes> getRecommendKeyword(List<Integer> randomIdxList) throws BaseException {
        try {
            List<GetRecommendKeywordRes> recommendKeywordList = new ArrayList<>();
            for (int randomIdx : randomIdxList) {
                GetRecommendKeywordRes keyword = boardDao.getRecommendKeyword(randomIdx);
                recommendKeywordList.add(keyword);
            }
            return recommendKeywordList;

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardQuizRes> getBoardQuiz(int boardIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            return boardDao.getBoardQuiz(boardIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardQuizAnswerRes> getBoardQuizAnswers(int boardIdx, int quizIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            return boardDao.getBoardQuizAnswers(boardIdx, quizIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardRes> getBoardsByCategoryIdx(int categoryIdx, int order) throws BaseException {
        checkCategoryIdxRange(categoryIdx);

        try {
            if (order == ORDER_BY_VIEW) {
                return boardDao.getBoardsByCategoryIdxOrderByView(categoryIdx);
            }
            if (order == ORDER_BY_LIKE) {
                return boardDao.getBoardsByCategoryIdxOrderByLike(categoryIdx);
            }
            return boardDao.getBoardsByCategoryIdx(categoryIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
