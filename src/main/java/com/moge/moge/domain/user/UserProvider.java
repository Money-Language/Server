package com.moge.moge.domain.user;

import com.moge.moge.domain.user.model.User;
import com.moge.moge.domain.user.model.req.PostLoginReq;
import com.moge.moge.domain.user.model.res.GetUserFollowRes;
import com.moge.moge.domain.user.model.res.PostLoginRes;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.config.security.SHA256;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.moge.moge.global.exception.BaseResponseStatus.*;

@Service
public class UserProvider {

    private final UserDao userDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService;
    }

    public int checkEmail(String email) throws BaseException {
        try{
            return userDao.checkEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkNickname(String nickname) throws BaseException {
        try {
            return userDao.checkNickname(nickname);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public PostLoginRes login(PostLoginReq postLoginReq) throws BaseException {
        User user = userDao.getPwd(postLoginReq);
        String encryptPwd;
        try {
            encryptPwd = new SHA256().encrypt(postLoginReq.getPassword());
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }

        if (user.getPassword().equals(encryptPwd)) {
            int userIdx = user.getUserIdx();
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx, jwt);
        } else {
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    public int checkCertifiedEmail(String email) throws BaseException {
        try{
            return userDao.checkCertifiedEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCertifiedTime(String email) throws BaseException {
        try{
            return userDao.checkCertifiedTime(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean checkCertifiedCode(String email, String code) throws BaseException {
        try{
            return userDao.checkCertifiedCode(email, code);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserFollowRes> getUserFollowings(int userIdx, int page) throws BaseException {
        try {
            return userDao.getUserFollowings(userIdx, page);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetUserFollowRes> getUserFollowers(int userIdx, int page) throws BaseException {
        try {
            return userDao.getUserFollowers(userIdx, page);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkUser(int userIdx) throws BaseException {
        try {
            return userDao.checkUser(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);

        }
    }
}
