package com.moge.moge.domain.quiz;

import com.moge.moge.domain.quiz.dto.req.PostBoardReq;
import com.moge.moge.domain.quiz.dto.res.PostBoardRes;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static com.moge.moge.global.exception.BaseResponseStatus.DATABASE_ERROR;

@Service
public class QuizService {

    private final QuizDao quizDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public QuizService(QuizDao quizDao) {
        this.quizDao = quizDao;
    }

    public PostBoardRes createBoards(int userIdx, PostBoardReq postBoardReq) throws BaseException {
        try {
            // title , categoryIdx null 체크

            return quizDao.createBoard(userIdx, postBoardReq);



        } catch(Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
