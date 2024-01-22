package com.example.myrealog.auth;

import com.example.myrealog.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static com.example.myrealog.auth.OAuthProvider.GOOGLE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login/oauth")
public class OAuthController {

    public static final int SEVEN_DAYS = 7 * 24 * 60 * 60;
    private final OAuthService oAuthService;
    private final UserRepository userRepository;

    @GetMapping("/google") // http://localhost:8080/login/oauth/google
    public void redirectToOAuthServer(HttpServletResponse response) throws IOException {
        response.sendRedirect(GOOGLE.getOAuthLoginUrl());
    }

    @GetMapping("/callback/google")
    public ResponseEntity<Void> login(@RequestParam("code") String code) {
        final String userEmail = oAuthService.getUserEmail(code);
        final String token = oAuthService.generateToken(userEmail);

        String redirectUrl = "http://localhost:3000";
        if (userRepository.existsByEmail(userEmail)) {
            redirectUrl += "/redirect";
        } else {
            redirectUrl += "/signup?email=" + userEmail;
        }

        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
                .maxAge(SEVEN_DAYS)
                .httpOnly(true)
//                .secure(true) // HTTPS 환경에서 사용
                .path("/")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", redirectUrl);
        headers.add("Set-Cookie", cookie.toString());
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).build();
    }
}
