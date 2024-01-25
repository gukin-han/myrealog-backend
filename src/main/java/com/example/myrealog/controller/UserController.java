package com.example.myrealog.controller;

import com.example.myrealog.auth.OAuthService;
import com.example.myrealog.dto.request.SignupFormRequest;
import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
import com.example.myrealog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final OAuthService oAuthService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(HttpServletRequest request) {
        final String token = request.getHeader("Authorization").substring(7);
        final String email = oAuthService.validateTokenAndGetEmail(token);
        final Optional<User> user = userRepository.findUserByEmail(email);

        return user.map(u -> ResponseEntity.ok(new MeDto(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public void signUp(@RequestBody @Valid SignupFormRequest signupForm, HttpServletRequest request) {
        final String token = request.getHeader("Authorization").substring(7);
        final String email = oAuthService.validateTokenAndGetEmail(token);

        final Profile profile = new Profile(signupForm.getDisplayName(), signupForm.getBio());
        final User user = new User(email, signupForm.getUsername());

        userService.signup(user, profile);
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
