package com.moge.moge.domain.user.model.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostEmailCheckReq {
    private String email;
    private String code;
}
