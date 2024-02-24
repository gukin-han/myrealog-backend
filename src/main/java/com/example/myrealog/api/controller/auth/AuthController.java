package com.example.myrealog.api.controller.auth;

import com.example.myrealog.auth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final OAuthService oAuthService;

    @GetMapping("/api/v1/signin/oauth/callback/google")
    public ResponseEntity<?> login(@RequestParam("code") String code) {
        return oAuthService.signInOrGetSignUpToken(code);
    }
}
