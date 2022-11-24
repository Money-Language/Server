package com.moge.moge.domain.user.controller;

import com.moge.moge.domain.user.service.UserProvider;
import com.moge.moge.domain.user.model.req.*;
import com.moge.moge.domain.user.model.res.*;
import com.moge.moge.domain.user.service.UserService;
import com.moge.moge.global.common.BaseResponse;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import com.moge.moge.global.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;
import static com.moge.moge.global.util.Constants.CATEGORY_SIZE;
import static com.moge.moge.global.util.Constants.PAGE_RANGE;
import static com.moge.moge.global.util.ValidationRegex.*;

@RestController
@RequestMapping("/app/users")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private final UserProvider userProvider;
    @Autowired private final UserService userService;
    @Autowired private final JwtService jwtService;
    private final ValidationUtils validationUtils;

    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService, ValidationUtils validationUtils){
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
        this.validationUtils = validationUtils;
    }

    /**
     * 회원 가입 API
     * [POST] /users/sign-up
     * @return BaseResponse<PostUserRes>
     */
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
            if (!isRegexNickname(postUserReq.getNickname())) {
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
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

    /**
     * 로그인 API
     * [POST] /users/login
     * @return BaseResponse<PostLoginRes>
     */
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

    /**
     * 패스워드 변경 API
     * [PATCH] /users/password
     * @return BaseResponse<String>
     */
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
            validationUtils.validateJwtToken(userIdx);
            userService.updatePassword(userIdx, patchUserPasswordReq);
            return new BaseResponse<>(SUCCESS_UPDATE_PASSWORD);
        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 관심 키워드 설정 API
     * [POST] /users/{userIdx}/keyword
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/{userIdx}/keyword")
    public BaseResponse<String> createUserKeyword(@PathVariable("userIdx") int userIdx, @RequestBody PostUserKeywordReq postUserKeywordReq) {
        try {
            validationUtils.validateJwtToken(userIdx);
            validationUtils.validateSize(postUserKeywordReq.getCategoryIdx().size(), CATEGORY_SIZE);
            userService.createUserKeyword(userIdx, postUserKeywordReq);
            return new BaseResponse<>(SUCCESS_CREATE_KEYWORD);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 관심 키워드 수정 API
     * [PATCH] /users/{userIdx}/keyword
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/keyword")
    public BaseResponse<String> updateUserKeyword(@PathVariable("userIdx") int userIdx, @RequestBody PatchUserKeywordReq patchUserKeywordReq) {
        try {
            validationUtils.validateJwtToken(userIdx);
            validationUtils.validateSize(patchUserKeywordReq.getCategoryIdx().size(), CATEGORY_SIZE);
            userService.updateUserKeyword(userIdx, patchUserKeywordReq);
            return new BaseResponse<>(SUCCESS_UPDATE_KEYWORD);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 관심 키워드 조회 API
     * [GET] /users/{userIdx}/keyword
     * @return BaseResponse<List<GetUserKeywordRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/keyword")
    public BaseResponse<List<GetUserKeywordRes>> getUserKeyword(@PathVariable("userIdx") int userIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            return new BaseResponse<>(userService.getUserKeyword(userIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로필 조회 API
     * [GET] /users/{userIdx}/profile
     * @return BaseResponse<GetUserProfileRes>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/profile")
    public BaseResponse<GetUserProfileRes> getUserProfile(@PathVariable("userIdx") int userIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            return new BaseResponse<>(userProvider.getUserProfile(userIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 프로필 사진, 닉네임 변경 API
     * [PATCH] /users/{userIdx}/profile
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/profile")
    public BaseResponse<String> updateProfile(@PathVariable("userIdx") int userIdx,
                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
                                              @RequestPart(value = "nickname") String nickname) {
        try {
            validationUtils.validateJwtToken(userIdx);
            userService.updateProfile(userIdx, profileImage, nickname);
            return new BaseResponse<>(SUCCESS_UPDATE_PROFILE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 프로필 사진 삭제 API
     * [DELETE] /users/{userIdx}/profile
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}/profile")
    public BaseResponse<String> deleteProfileImage(@PathVariable("userIdx") int userIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            userService.deleteProfileImage(userIdx);
            return new BaseResponse<>(SUCCESS_DELETE_USER_PROFILE_IMAGE);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로우 등록 및 취소 API
     * [POST] /users/follow
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/follow")
    public BaseResponse<String> createUserFollow(@RequestBody PostUserFollowReq postUserFollowReq) {
        try {
            userService.createUserFollow(postUserFollowReq.getUserIdx(), postUserFollowReq.getFollowingIdx());
            return new BaseResponse<>(SUCCESS_CREATE_FOLLOW);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로잉 조회 API
     * [GET] /users/{userIdx}/following
     * @return BaseResponse<List<GetUserFollowRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/following")
    public BaseResponse<List<GetUserFollowRes>> getUserFollowings(@PathVariable("userIdx") int userIdx,
                                                                  int page) {
        try {
            validationUtils.validateJwtToken(userIdx);
            validationUtils.validateRange(page, PAGE_RANGE);
            return new BaseResponse<>(userProvider.getUserFollowings(userIdx, page));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로워 조회 API
     * [GET] /users/{userIdx}/follower
     * @return BaseResponse<List<GetUserFollowRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/follower")
    public BaseResponse<List<GetUserFollowRes>> getUserFollowers(@PathVariable("userIdx") int userIdx, int page) {
        try {
            validationUtils.validateJwtToken(userIdx);
            validationUtils.validateRange(page, PAGE_RANGE);
            return new BaseResponse<>(userProvider.getUserFollowers(userIdx, page));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저가 좋아요를 누른 게시글 조회 API
     * [GET] /users/{userIdx}/boards/like
     * @return BaseResponse<List<GetUserBoardLikeRes>>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/boards/like")
    public BaseResponse<List<GetUserBoardLikeRes>> getUserBoardLikes(@PathVariable("userIdx") int userIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            return new BaseResponse<>(userProvider.getUserBoardLike(userIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저가 작성한 게시글 조회 API
     * [GET] /users/{userIdx}/boards
     * @return BaseResponse<>
     */
    @ResponseBody
    @GetMapping("/{userIdx}/boards")
    public BaseResponse<List<GetUserBoardRes>> getUserBoards(@PathVariable("userIdx") int userIdx,
                                                             @RequestParam(required = false) Integer categoryIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            if (categoryIdx == null) {
                return new BaseResponse<>(userProvider.getUserBoards(userIdx));
            }
            return new BaseResponse<>(userProvider.getUserBoardsByCategory(userIdx, categoryIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 탈퇴 API
     * [DELETE] /users/{userIdx}
     * @return BaseResponse<String>
     */
    @ResponseBody
    @DeleteMapping("/{userIdx}")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            userService.deleteUser(userIdx);
            return new BaseResponse<>(SUCCESS_DELETE_USER);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 패스워드 validation API
     * [POST] /users/validate-password
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/validate-password")
    public BaseResponse<String> validateEmail(@RequestBody PostUserPasswordValidateReq postUserPasswordValidateReq) {
        if (postUserPasswordValidateReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        if (postUserPasswordValidateReq.getRePassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_REPASSWORD);
        }
        if (!isRegexPassword(postUserPasswordValidateReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        if (!postUserPasswordValidateReq.getPassword().equals(postUserPasswordValidateReq.getRePassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_REPASSWORD);
        }
        return new BaseResponse<>(SUCCESS_CHECK_PASSWORD);

    }

    /**
     * 이메일 validation API
     * [POST] /users/validate-email
     * @return BaseResponse<String>
     */
    @ResponseBody
    @PostMapping("/validate-email")
    public BaseResponse<String> validateEmail(@RequestParam("email") String email) {
        try {
            if (email == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            if (!isRegexEmail(email)) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            userProvider.checkEmail(email);
            return new BaseResponse<>(SUCCESS_CHECK_EMAIL);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 닉네임 validation API
     * [POST] /users/validate-nickname
     * @return BaseResponse<String>
     */    @ResponseBody
    @PostMapping("/validate-nickname")
    public BaseResponse<String> validateNickname(@RequestParam("nickname") String nickname) {
        try {
            if (nickname == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_NICKNAME);
            }
            if (!isRegexNickname(nickname)) {
                return new BaseResponse<>(POST_USERS_INVALID_NICKNAME);
            }
            userProvider.checkNickname(nickname);
            return new BaseResponse<>(SUCCESS_CHECK_NICKNAME);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

}
