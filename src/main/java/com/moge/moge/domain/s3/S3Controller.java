package com.moge.moge.domain.s3;

import com.moge.moge.global.common.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.SUCCESS_DELETE_AWS_S3;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app/s3")
public class S3Controller {

    private final S3Service awsS3Service;

    @PostMapping("/file")
    public BaseResponse<List<String>> uploadFile(@RequestPart List<MultipartFile> multipartFile) {
        return new BaseResponse<>(awsS3Service.uploadFile(multipartFile));
    }

    @DeleteMapping("/file")
    public BaseResponse<String> deleteFile(@RequestParam String fileName) {
        awsS3Service.deleteFile(fileName);
        return new BaseResponse<>(SUCCESS_DELETE_AWS_S3);
    }
}
