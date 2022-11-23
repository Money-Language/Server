package com.moge.moge.domain.mail.service;

import com.moge.moge.domain.mail.dao.MailDao;
import com.moge.moge.global.config.security.JwtService;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.moge.moge.global.exception.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MailProvider {

    private final MailDao mailDao;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public MailProvider(MailDao mailDao, JwtService jwtService) {
        this.mailDao = mailDao;
    }

    public int checkCertifiedEmail(String email) throws BaseException {
        try{
            return mailDao.checkCertifiedEmail(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public int checkCertifiedTime(String email) throws BaseException {
        try{
            return mailDao.checkCertifiedTime(email);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public boolean checkCertifiedCode(String email, String code) throws BaseException {
        try{
            return mailDao.checkCertifiedCode(email, code);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
