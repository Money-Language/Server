package com.moge.moge.domain.board;

import com.moge.moge.domain.user.UserProvider;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            System.out.println("====" + userIdxByJwt + "===");

            if (userProvider.checkUser(userIdxByJwt) == 0) {
                return new BaseResponse<>(USERS_EMPTY_USER_IDX);
            }
            boardService.createBoardLike(boardIdx, userIdxByJwt);
            return new BaseResponse<>(SUCCESS_CREATE_BOARD_LIKE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
