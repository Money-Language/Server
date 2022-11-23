package com.moge.moge.domain.board.service;

import com.moge.moge.domain.board.dao.BoardDao;
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
        //try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }
            return boardDao.getBoardComments(boardIdx);

        //} catch (BaseException exception) {
        //    throw new BaseException(DATABASE_ERROR);
        //}
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
        //try {
            List<GetRecommendKeywordRes> recommendKeywordList = new ArrayList<>();
            for (int randomIdx : randomIdxList) {
                GetRecommendKeywordRes keyword = boardDao.getRecommendKeyword(randomIdx);
                recommendKeywordList.add(keyword);
            }
            return recommendKeywordList;

        //} catch (Exception exception) {
        //    throw new BaseException(DATABASE_ERROR);
        //}
    }
}
