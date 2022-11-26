package com.moge.moge.domain.user.controller;

import com.moge.moge.domain.user.service.UserProvider;
import com.moge.moge.domain.user.dto.req.*;
import com.moge.moge.domain.user.dto.res.*;
import com.moge.moge.domain.user.service.UserService;
import com.moge.moge.global.common.BaseResponse;
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

@RestController
@RequestMapping("/app/users")
public class UserController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired private final UserProvider userProvider;
    @Autowired private final UserService userService;
    @Autowired private final ValidationUtils validationUtils;

    public UserController(UserProvider userProvider, UserService userService, ValidationUtils validationUtils){
        this.userProvider = userProvider;
        this.userService = userService;
        this.validationUtils = validationUtils;
    }

    /* 회원가입*/
    @ResponseBody
    @PostMapping("/sign-up")
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        try {
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
        try {
            PostLoginRes postLoginRes = userService.login(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /* 비밀번호 수정 */
    @ResponseBody
    @PatchMapping("/{userIdx}/password")
    public BaseResponse<String> updatePassword(@PathVariable("userIdx") int userIdx,
                                               @RequestBody PatchUserPasswordReq patchUserPasswordReq) {
        try {
            validationUtils.validateJwtToken(userIdx);
            userService.updatePassword(userIdx, patchUserPasswordReq);
            return new BaseResponse<>(SUCCESS_UPDATE_PASSWORD);
        } catch(BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /* 키워드 설정 */
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

    /* 키워드 수정*/
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

    /* 키워드 조회 */
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

    /* 프로필 조회 */
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

    /* 프로필 수정 (사진, 닉네임)*/
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

    /* 프로필 사진 삭제*/
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

    /* 팔로우 등록, 취소 */
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

    /* 팔로잉 조회 */
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

    /* 팔로워 조회 */
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

    /* 유저가 좋아요 누른 게시글 조회 */
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

    /* 유저가 작성한 게시글 조회 */
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

    /* 유저 탈퇴 */
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

    /* 유저 포인트 조회 */
    @ResponseBody
    @GetMapping("/{userIdx}/points")
    public BaseResponse<GetUserPointRes> getUserPoints(@PathVariable("userIdx") int userIdx) {
        try {
            validationUtils.validateJwtToken(userIdx);
            return new BaseResponse<>(userService.getUserPoints(userIdx));
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @PostMapping("/validate-password")
    public BaseResponse<String> validatePassword(@RequestBody PostUserPasswordValidateReq postUserPasswordValidateReq) {
        try {
            userProvider.checkPassword(postUserPasswordValidateReq);
            return new BaseResponse<>(SUCCESS_CHECK_PASSWORD);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/validate-email")
    public BaseResponse<String> validateEmail(@RequestParam("email") String email) {
        try {
            userProvider.checkEmail(email);
            return new BaseResponse<>(SUCCESS_CHECK_EMAIL);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @ResponseBody
    @PostMapping("/validate-nickname")
    public BaseResponse<String> validateNickname(@RequestParam("nickname") String nickname) {
        try {
            userProvider.checkNickname(nickname);
            return new BaseResponse<>(SUCCESS_CHECK_NICKNAME);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
