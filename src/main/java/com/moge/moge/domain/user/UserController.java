package com.moge.moge.domain.user;

import com.moge.moge.domain.user.model.req.PatchUserPasswordReq;
import com.moge.moge.domain.user.model.req.PostEmailCheckReq;
import com.moge.moge.domain.user.model.req.PostLoginReq;
import com.moge.moge.domain.user.model.req.PostUserReq;
import com.moge.moge.domain.user.model.res.PostLoginRes;
import com.moge.moge.domain.user.model.res.PostUserRes;
import com.moge.moge.domain.user.service.MailService;
import com.moge.moge.domain.user.service.UserService;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;

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
    @Autowired private final MailService mailService;

    public UserController(UserProvider userProvider, UserService userService, MailService mailService, JwtService jwtService){
        this.userProvider = userProvider;
        this.userService = userService;
        this.mailService = mailService;
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

    /* 로그인 */
    @ResponseBody
    @PostMapping("/login")
    public BaseResponse<PostLoginRes> login(@RequestBody PostLoginReq postLoginReq) {
        if (postLoginReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        if (postLoginReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if (!isRegexEmail(postLoginReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }

        try {
            PostLoginRes postLoginRes = userProvider.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /* 이메일 인증 메일 발송 */
    @ResponseBody
    @PostMapping("/send-email")
    public BaseResponse<String> sendEmail(@RequestParam("email") String email) {
        if (email != null) {
            if (!isRegexEmail(email)) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
        }

        try {
            String code = mailService.sendCertifiedMail(email);
            mailService.insertCertifiedCode(email, code);
            return new BaseResponse<>(code);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* 패스워드 변경 */
    @ResponseBody
    @PatchMapping("/{userIdx}/password")
    public BaseResponse<String> updatePassword(@PathVariable("userIdx") int userIdx,
                                               @RequestBody PatchUserPasswordReq patchUserPasswordReq) {

        if (!isRegexPassword(patchUserPasswordReq.getModPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if (!patchUserPasswordReq.getModPassword().equals(patchUserPasswordReq.getReModPassword())) {
            return new BaseResponse<>(POST_USERS_NEW_PASSWORD_NOT_CORRECT);
        }

        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if (userIdxByJwt != userIdx) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.updatePassword(userIdx, patchUserPasswordReq);
            return new BaseResponse<>(SUCCESS_UPDATE_PASSWORD);
        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /* 이메일 중복 확인 */
    @ResponseBody
    @PostMapping("/login/check-email")
    public BaseResponse<String> checkEmail(@RequestBody PostEmailCheckReq postEmailCheckReq) throws BaseException {
        if (postEmailCheckReq.getEmail() != null) {
            if (!isRegexEmail(postEmailCheckReq.getEmail())) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
        }

        if (userProvider.checkCertifiedEmail(postEmailCheckReq.getEmail()) == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_CERTIFIED_EMAIL);
        }

        int timeDiff = userProvider.checkCertifiedTime(postEmailCheckReq.getEmail());
        if (timeDiff >= 1000) {
            return new BaseResponse<>(FAILED_TO_CERTIFY_TIME);
        }

        if (!(userProvider.checkCertifiedCode(postEmailCheckReq.getEmail(), postEmailCheckReq.getCode()))) {
            return new BaseResponse<>(FAILED_TO_CERTIFY_CODE);
        }

        return new BaseResponse<>(SUCCESS_CHECK_CERTIFY_EMAIL);
    }

}
