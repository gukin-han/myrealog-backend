package com.example.myrealog.api.controller.user;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.api.service.user.response.UserResponse;
import com.example.myrealog.v1.common.dto.request.UserSignUpRequest;
import com.example.myrealog.auth.service.response.AuthResponse;
import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.service.OAuthService;
import com.example.myrealog.auth.UserPrincipal;
import com.example.myrealog.v1.common.dto.response.ResponseWrapper;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.api.service.user.UserService;
import com.example.myrealog.v1.common.utils.WebUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;

    @PostMapping("/api/v1/users/signup")
    public ResponseEntity<?> signUp(@Authorized UserPrincipal principal,
                                    @RequestBody @Valid UserSignUpRequest request) {
        final User signedUpUser = userService.signUp(request, principal.getEmail(), LocalDateTime.now());
        final AuthResponse authToken = oAuthService.signIn(signedUpUser);
        final ResponseCookie responseCookie = WebUtils.generateCookie(authToken.getType().name(), authToken.getValue());

        return WebUtils.buildRedirectResponse(
                "/redirect",
                responseCookie,
                HttpStatus.CREATED,
                ResponseWrapper.of(authToken)
        );
    }

    @GetMapping("/api/v1/users/me")
    public ApiResponse<UserResponse> getMe(@Authorized UserPrincipal principal) {
        return ApiResponse.ok(userService.getMe(principal.getUserId()));
    }

}
