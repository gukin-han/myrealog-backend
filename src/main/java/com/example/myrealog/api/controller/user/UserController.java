package com.example.myrealog.api.controller.user;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.api.service.user.response.UserResponse;
import com.example.myrealog.v1.common.dto.request.UserSignupRequest;
import com.example.myrealog.v1.common.dto.response.AuthTokenResponse;
import com.example.myrealog.v1.common.auth.Authorized;
import com.example.myrealog.v1.common.auth.OAuthService;
import com.example.myrealog.v1.common.auth.UserPrincipal;
import com.example.myrealog.v1.common.dto.response.ResponseWrapper;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.api.service.user.UserService;
import com.example.myrealog.v1.common.utils.JwtUtils;
import com.example.myrealog.v1.common.utils.WebUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;

    @GetMapping("/api/v1/users/me")
    public ApiResponse<UserResponse> getMe(@Authorized UserPrincipal principal) {
        return ApiResponse.ok(userService.getMe(principal.getUserId()));
    }

    @PostMapping("/api/v1/users")
    public ResponseEntity<?> signUp(@RequestBody @Valid UserSignupRequest request,
                                    HttpServletRequest httpServletRequest) {

        final String signupToken = WebUtils.extractTokenFromRequest(httpServletRequest);
        final String email = JwtUtils.validateJwtAndGetSubject(signupToken); // can throw error

        final User signedUpUser = userService.signUp(request, email);

        final AuthTokenResponse authToken = oAuthService.signIn(signedUpUser);
        final ResponseCookie responseCookie = WebUtils.generateCookie(authToken.getType().name(), authToken.getValue());

        return WebUtils.buildRedirectResponse(
                "/redirect",
                responseCookie,
                HttpStatus.CREATED,
                ResponseWrapper.of(authToken)
        );
    }
}
