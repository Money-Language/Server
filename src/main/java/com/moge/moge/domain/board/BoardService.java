package com.moge.moge.domain.board;

import com.moge.moge.domain.s3.S3Service;
import com.moge.moge.domain.user.UserDao;
import com.moge.moge.domain.user.UserProvider;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import com.moge.moge.global.exception.BaseResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void createBoardLike(int boardIdx) throws BaseException {
        try {
            if (boardDao.checkBoardExists(boardIdx) == 0) {
                throw new BaseException(BOARD_NOT_EXISTS);
            }

            // 좋아요가 눌려있으면
            if (boardDao.checkBoardLikeExists(boardIdx) == 1) {
                boardDao.deleteBoardLike(boardIdx);
            } else { // 안눌려있으면
                if (boardDao.checkBoardLikeStatus(boardIdx) == 1) {
                    boardDao.updateBoardLikeStatus(boardIdx);
                } else {
                    int result = boardDao.createBoardLike(boardIdx);
                    if (result == 0) {
                        throw new BaseException(FAILED_TO_CREATE_BOARD_LIKE);
                    }
                }
            }
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
