package com.moge.moge.domain.mail.service;

import com.moge.moge.domain.user.dao.UserDao;
import com.moge.moge.global.exception.BaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage.RecipientType;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import static com.moge.moge.global.exception.BaseResponseStatus.DATABASE_ERROR;

@Service
public class MailService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDao userDao;
    private JavaMailSender emailSender;
    private String ePw;

    @Autowired
    public MailService(UserDao userDao, JavaMailSender emailSender) {
        this.userDao = userDao;
        this.emailSender = emailSender;
    }

    public MimeMessage createMessage(String to) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(RecipientType.TO, to);
        message.setSubject("MOGE 모지 회원가입 이메일 인증");
        String messageContents = createMailContents();
        message.setText(messageContents, "utf-8", "html");
        message.setFrom(new InternetAddress("dpwls3976@naver.com", "MOGE"));
        return message;
    }

    private String createMailContents() {
        String message = "";
        message += "<div style='margin:100px;'>";
        message += "<h1> 안녕하세요 </h1>";
        message += "<h1> MOGE 모지입니다</h1>";
        message += "<br>";
        message += "<p>아래 코드를 회원가입 창으로 돌아가 입력해주세요<p>";
        message += "<br>";
        message += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        message += "<h3 style='color:blue;'> 회원가입 인증 코드입니다.</h3>";
        message += "<div style='font-size:130%'>";
        message += "CODE : <strong>";
        message += ePw + "</strong><div><br/> ";
        message += "</div>";
        return message;
    }

    public String sendCertifiedMail(String to) throws Exception {
        ePw = createKey();
        MimeMessage message = createMessage(to);

        try {
            emailSender.send(message);
        } catch (MailException es) {
            es.printStackTrace();
            throw new IllegalArgumentException();
        }
        return ePw;
    }

    private String createKey() {
        StringBuffer key = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0:
                    key.append((char) ((int) (random.nextInt(26)) + 97));
                    break;
                case 1:
                    key.append((char) ((int) (random.nextInt(26)) + 65));
                    break;
                case 2:
                    key.append((random.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    public void insertCertifiedCode(String email, String code) throws BaseException {
        try {
            int result = userDao.insertCertifiedCode(email, code);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

