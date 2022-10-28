package com.moge.moge.domain.user.model.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
