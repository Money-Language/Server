package com.moge.moge.domain.user.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatchUserPasswordReq {
    private String currentPassword; // 현재 비밀번호
    private String modPassword; // 변경하고자하는 패스워드
    private String reModPassword; // 재확인을 위한 패스워드
}
