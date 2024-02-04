package com.example.myrealog.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

    private static final long EXPIRATION_TIME = 86400000;
    private static final String SECRET_KEY = "a0daASD0as0daLKOIWN123Liqpvm211340vxlsewrLiqpvm21134040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLi"; // 비밀 키 설정
    private static final SecretKey SECRET = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public static String generateJwt(String subject) {
        return Jwts
                .builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String validateJwtAndGetSubject(String token) throws JwtException {
        final Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(SECRET)
                .build()
                .parseSignedClaims(token);

        return claimsJws
                .getPayload()
                .getSubject();
    }
}
