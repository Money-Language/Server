package com.moge.moge.domain.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PostUserReq {
    private String email;
    private String nickname;
    private String password;
    private String rePassword; // 패스워드 재확인
    private Integer contract1; // 약관
    private Integer contract2;
    private Integer contract3;
    private Integer contract4;
}
