package com.moge.moge.global.util;

import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.springframework.stereotype.Component;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@Component
public class ValidationUtils {

    private final JwtService jwtService;

    public ValidationUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public BaseResponse<Integer> validateJwtToken(int userIdx) throws BaseException {
        int userIdxByJwt = jwtService.getUserIdx();
        if (userIdxByJwt != userIdx) {
            return new BaseResponse<>(INVALID_USER_JWT);
        }
        return new BaseResponse<>(userIdxByJwt);
    }

    public BaseResponse validateSize(int target, int size) {
        if (target != size) {
            return new BaseResponse<>(POST_USERS_CATEGORY_NUM);
        }
        return null;
    }

    public BaseResponse validateRange(int target, int range) {
        if  (target <= range) {
            return new BaseResponse(POST_FOLLOW_INVALID_PAGE);
        }
        return null;
    }
}
