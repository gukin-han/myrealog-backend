package com.example.myrealog.common.auth;

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

    @GetMapping("/google") // http://localhost:8080/api/v1/signin/oauth/google
    public void redirectToOAuthServer(HttpServletResponse response) throws IOException {
        response.sendRedirect(OAuthProvider.GOOGLE.getOAuthLoginUrl());
    }

    @GetMapping("/callback/google") // http://localhost:8080/api/v1/signin/oauth/callback/google
    public ResponseEntity<?> login(@RequestParam("code") String code) {
        return oAuthService.signInOrGetSignUpToken(code);
    }
}
