package com.example.myrealog.controller;

import com.example.myrealog.auth.OAuthService;
import com.example.myrealog.dto.request.SignUpFormRequest;
import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
import com.example.myrealog.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OAuthService oAuthService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {

        try {
            final String accessToken = request.getHeader("Authorization").substring(7);
            final String userId = oAuthService.validateTokenAndGetSubject(accessToken); // 유효기간 만료 및 위변조 에러 처리

            final Optional<User> user = userRepository.findById(Long.parseLong(userId));
            return user.map(u -> ResponseEntity.ok(new MeDto(u)))
                    .orElseGet(() -> {
                        log.error("존재하지 않는 유저입니다.");
                        return ResponseEntity.badRequest().build();
                    });

        } catch (JwtException e) {
            log.error("액세스 토큰 검증에 실패했습니다???.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpFormRequest signUpForm, HttpServletRequest request) {
        final String signUpToken = request.getHeader("Authorization").substring(7);
        final String email = oAuthService.validateTokenAndGetSubject(signUpToken);

        final User signedUpUser = userService.signUp(
                new User(email, signUpForm.getUsername()),
                new Profile(signUpForm.getDisplayName(), signUpForm.getBio()));

        return oAuthService.signIn(signedUpUser);
    }

    @Data
    static class MeDto {
        private String username;
        private String displayName;
        private String avatarUrl;

        public MeDto(User user) {
            username = user.getUsername();
            displayName = user.getProfile().getDisplayName();
            avatarUrl = user.getProfile().getAvatarUrl();
        }
    }
}
