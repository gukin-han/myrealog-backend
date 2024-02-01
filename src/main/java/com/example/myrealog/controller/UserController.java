package com.example.myrealog.controller;

import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.OAuthService;
import com.example.myrealog.dto.request.SignUpFormRequest;
import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;

    @GetMapping("/me")
    public ResponseEntity<MeDto> getMe(@Authorized User user) {
        return ResponseEntity.ok(new MeDto(user));
    }

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpFormRequest signUpForm,
                                       HttpServletRequest request) {

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
