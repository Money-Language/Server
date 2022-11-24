package com.moge.moge.domain.mail.controller;

import com.moge.moge.domain.mail.service.MailProvider;
import com.moge.moge.domain.mail.service.MailService;
import com.moge.moge.domain.mail.model.req.PostEmailCheckReq;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.exception.BaseResponseStatus.SUCCESS_CHECK_CERTIFY_EMAIL;
import static com.moge.moge.global.util.ValidationRegex.isRegexEmail;
import static com.moge.moge.global.util.ValidationRegex.isRegexEmailCode;

@RestController
@RequestMapping("/app/users")
public class MailController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MailService mailService;
    private final MailProvider mailProvider;

    public MailController(MailService mailService, MailProvider mailProvider){
        this.mailService = mailService;
        this.mailProvider = mailProvider;
    }

    /**
     * 이메일 인증 발송 API
     * [POST] /users/send-email
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/send-email")
    public BaseResponse<String> sendEmail(@RequestParam("email") String email) {
       // if (email != null) {
       //     if (!isRegexEmail(email)) {
       //         return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
       //     }
       // }
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

    /**
     * 이메일 중복 확인 API
     * [POST] /users/login/check-email
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/login/check-email")
    public BaseResponse<String> checkEmail(@RequestBody PostEmailCheckReq postEmailCheckReq) throws BaseException {
        //if (postEmailCheckReq.getEmail() != null) {
        //    if (!isRegexEmail(postEmailCheckReq.getEmail())) {
        //        return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        //    }
        //}
        //if (!isRegexEmailCode(postEmailCheckReq.getCode())) {
        //    return new BaseResponse<>(POST_USERS_INVALID_EMAIL_CODE);
        //}
        if (mailProvider.checkCertifiedEmail(postEmailCheckReq.getEmail()) == 0) {
            return new BaseResponse<>(POST_USERS_EMPTY_CERTIFIED_EMAIL);
        }
        int timeDiff = mailProvider.checkCertifiedTime(postEmailCheckReq.getEmail());
        if (timeDiff >= 1000) {
            return new BaseResponse<>(FAILED_TO_CERTIFY_TIME);
        }
        if (!(mailProvider.checkCertifiedCode(postEmailCheckReq.getEmail(), postEmailCheckReq.getCode()))) {
            return new BaseResponse<>(FAILED_TO_CERTIFY_CODE);
        }
        return new BaseResponse<>(SUCCESS_CHECK_CERTIFY_EMAIL);
    }

}
