package com.moge.moge.domain.user.model.req;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class PatchUserProfileReq {
    private List<MultipartFile> profileImage;
    private String nickname;
}
