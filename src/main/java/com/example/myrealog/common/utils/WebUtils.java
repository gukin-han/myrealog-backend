package com.example.myrealog.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import java.net.URI;

public class WebUtils {

    private static final int SEVEN_DAYS = 7 * 24 * 60 * 60;

    public static String extractTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(7);
    }

    public static ResponseEntity<?> buildRedirectResponse(String redirectUrl, ResponseCookie cookie, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.status(status).headers(headers).build();
    }

    public static ResponseEntity<?> buildRedirectResponse(String redirectUrl, ResponseCookie cookie, HttpStatus status, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.status(status).headers(headers).body(body);
    }

    public static ResponseCookie generateCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .maxAge(SEVEN_DAYS)
                .httpOnly(true)
//                .secure(true) // HTTPS 환경에서 사용
                .path("/")
                .build();
    }
}
