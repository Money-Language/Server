package com.moge.moge.domain.board;

import com.moge.moge.domain.board.model.GetBoardTop;
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

    public List<GetBoardTop> getBoardTopLike() throws BaseException {
        try {
            return boardDao.getBoardTopLike();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetBoardTop> getBoardTopView() throws BaseException {
        try {
            return boardDao.getBoardTopView();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
