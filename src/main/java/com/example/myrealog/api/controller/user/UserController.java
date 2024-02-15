package com.example.myrealog.api.controller.user;

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


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final OAuthService oAuthService;

    @GetMapping("/me")
    public ResponseEntity<?> getMe(@Authorized UserPrincipal principal) {
        final User user = userService.findUserAndProfileById(principal.getUserId());
        return ResponseEntity.ok(ResponseWrapper.of(new MeDto(user)));
    }

    @PostMapping
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
