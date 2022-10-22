package com.moge.moge.domain.board;

import com.moge.moge.global.config.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BoardProvider {

    private final BoardDao boardDao;
    private final JwtService jwtService;

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public BoardProvider(BoardDao boardDao, JwtService jwtService) {
        this.boardDao = boardDao;
        this.jwtService = jwtService;
    }
}
