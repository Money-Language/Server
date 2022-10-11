package com.moge.moge.global.config.secret;


import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class Secret {
    //public static String JWT_SECRET_KEY = "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    public static String JWT_SECRET_KEY = "UwKYibQQgkW7g-*k.ap9kje-wxBHb9wdXoBT4vnt4P3sJWt-Nu";

    public static Key getSigninKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

