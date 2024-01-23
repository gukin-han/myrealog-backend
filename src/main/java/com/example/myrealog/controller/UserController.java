package com.example.myrealog.controller;

import com.example.myrealog.auth.OAuthService;
import com.example.myrealog.dto.request.SignupFormRequest;
import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;

    @PostMapping
    public void signup(@RequestBody @Valid SignupFormRequest signupForm, HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        final String email = oAuthService.validateTokenAndGetEmail(token);

        final Profile profile = new Profile(signupForm.getDisplayName(), signupForm.getBio());
        final User user = new User(email, signupForm.getUsername());

        final User signupedUser = userService.signup(user, profile);
    }
}
