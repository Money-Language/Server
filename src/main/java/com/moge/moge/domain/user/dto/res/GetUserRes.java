package com.moge.moge.domain.user.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUserRes {
    private int userIdx;
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
    private String status;
    private String createdAt;
}
