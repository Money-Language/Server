package com.moge.moge.domain.fcm;

import com.moge.moge.domain.fcm.model.PushMessage;
import com.moge.moge.global.common.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.moge.moge.global.exception.BaseResponseStatus.SUCCESS_CHECK_CERTIFY_EMAIL;
import static com.moge.moge.global.exception.BaseResponseStatus.SUCCESS_PUSH_ALARM;

@RestController
@RequestMapping("/app/fcm")
public class FcmController {

    /**
     * 1. 오늘의 퀴즈 알림
     * 2. 내 퀴즈에 좋아요를 눌렀을 때
     * 3. 내 퀴즈에 댓글이 달렸을 때
     * 4. 내 댓글에 좋아요를 눌렀을 때
     * 5. 새로운 팔로우가 생겼을 때
     * */

    private final FcmService fcmService;

    public FcmController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @ResponseBody
    @PostMapping("/push")
    public BaseResponse<String> push(@RequestBody FcmDto fcmDto, @RequestParam("type") int type) {
        try {
            String title = null;
            String body = null;

            if (type == 1) {
                title = PushMessage.COMMENT_LIKE.getTitle();
                body = PushMessage.COMMENT_LIKE.getBody();
            }
            if (type == 2) {
                title = PushMessage.QUIZ_LIKE.getTitle();
                body = PushMessage.QUIZ_LIKE.getBody();
            }
            if (type == 3) {
                title = PushMessage.QUIZ_COMMENT.getTitle();
                body = PushMessage.QUIZ_COMMENT.getBody();
            }
            if (type == 4) {
                title = PushMessage.COMMENT_LIKE.getTitle();
                body = PushMessage.COMMENT_LIKE.getBody();
            }
            if (type == 5) {
                title = PushMessage.FOLLOW.getTitle();
                body = PushMessage.FOLLOW.getBody();
            }
            if (type == 6) {
                title = PushMessage.MENTION.getTitle();
                body = PushMessage.MENTION.getBody();
            }

            fcmService.sendMessageTo(fcmDto.getToken(), title, body);

            return new BaseResponse<>(SUCCESS_PUSH_ALARM);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
