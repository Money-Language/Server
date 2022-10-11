package com.moge.moge.domain.user;

import com.moge.moge.domain.user.model.PostUserReq;
import com.moge.moge.domain.user.model.PostUserRes;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.util.ValidationRegex.isRegexEmail;
import static com.moge.moge.global.util.ValidationRegex.isRegexPassword;

@RestController
@RequestMapping("/app/users")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private final UserProvider userProvider;
    @Autowired private final UserService userService;
    @Autowired private final JwtService jwtService;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /* 회원가입 */
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        try {
            if (postUserReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (postUserReq.getNickname() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
            }
            if (postUserReq.getPassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            if (postUserReq.getRePassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_REPASSWORD);
            }
            if (postUserReq.getContract1() == null && postUserReq.getContract2() == null && postUserReq.getContract3() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_TERMS);
            }
            if (!isRegexEmail(postUserReq.getEmail())) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            if (!isRegexPassword(postUserReq.getPassword())) {
                return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
            }
            if (!postUserReq.getPassword().equals(postUserReq.getRePassword())) {
                return new BaseResponse<>(POST_USERS_INVALID_REPASSWORD);
            }

            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);

        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
