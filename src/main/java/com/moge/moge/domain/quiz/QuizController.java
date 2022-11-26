package com.moge.moge.domain.quiz;

import com.moge.moge.domain.quiz.dto.req.PostBoardReq;
import com.moge.moge.domain.quiz.dto.req.PostQuizReq;
import com.moge.moge.domain.quiz.dto.res.PostBoardRes;
import com.moge.moge.domain.quiz.dto.res.PostQuizRes;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.exception.BaseException;
import com.moge.moge.global.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/quiz")
public class QuizController {

    private final ValidationUtils validationUtils;
    private final QuizService quizService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    public QuizController(ValidationUtils validationUtils, QuizService quizService) {
        this.validationUtils = validationUtils;
        this.quizService = quizService;
    }

    /* 퀴즈 게시물 등록 */
    @ResponseBody
    @PostMapping("/boards")
    public BaseResponse<PostBoardRes> createBoards(@RequestBody PostBoardReq postBoardReq) {
        try {
            int userIdx = validationUtils.checkJwtTokenExists();
            return new BaseResponse<>(quizService.createBoards(userIdx, postBoardReq));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /* 퀴즈 등록 */
    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostQuizRes> createQuiz(@RequestBody PostQuizReq postQuizReq) {
        try {
            int userIdx = validationUtils.checkJwtTokenExists();
            return new BaseResponse<>(quizService.createQuiz(postQuizReq));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
