package com.moge.moge.domain.fcm;

import com.moge.moge.domain.fcm.domain.FcmDto;
import com.moge.moge.global.common.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.moge.moge.global.exception.BaseResponseStatus.SUCCESS_CHECK_CERTIFY_EMAIL;

@RestController
@RequestMapping("/app/fcm")
public class FcmController {

    /**
     * 1. 오늘의 퀴즈 알림
     * 2. 내 퀴즈에 좋아요를 눌렀을 때
     * 3. 내 퀴즈에 댓글이 달렸을 때
     * 4. 나를 멘션했을 때
     * 5. 내 댓글에 좋아요를 눌렀을 때
     * 6. 새로운 팔로우가 생겼을 때
     * */

    @Autowired private final FcmService fcmService;

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @ResponseBody
    @PostMapping("/push")
    public BaseResponse<String> push(@RequestBody FcmDto fcmDto) {
        try {
            String accessToken = fcmService.getAccessToken();
            fcmService.sendMessageTo(accessToken,
                    fcmDto.getTitle(),
                    fcmDto.getBody()
            );
            return new BaseResponse<>(SUCCESS_CHECK_CERTIFY_EMAIL);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
