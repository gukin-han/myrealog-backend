package com.example.myrealog.auth.controller;

import com.example.myrealog.auth.OAuthProvider;
import com.example.myrealog.auth.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/signin/oauth")
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/google")
    public void redirectToOAuthServer(HttpServletResponse response) throws IOException {
        response.sendRedirect(OAuthProvider.GOOGLE.getOAuthLoginUrl());
    }

    @GetMapping("/callback/google")
    public ResponseEntity<?> login(@RequestParam("code") String code) {
        return oAuthService.signInOrGetSignUpToken(code);
    }
}
