package com.example.myrealog.controller;

import com.example.myrealog.auth.AuthToken;
import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.OAuthService;
import com.example.myrealog.auth.UserPrincipal;
import com.example.myrealog.dto.request.SignUpFormRequest;
import com.example.myrealog.dto.response.ResponseDto;
import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.service.UserService;
import com.example.myrealog.utils.JwtUtils;
import com.example.myrealog.utils.WebUtils;
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
        final ResponseDto<MeDto> body = ResponseDto.ok(new MeDto(user));
        return ResponseEntity.ok(body);
    }

    @PostMapping
    public ResponseEntity<?> signUp(@RequestBody @Valid SignUpFormRequest signUpForm,
                                    HttpServletRequest request) {
        final String signupToken = WebUtils.extractTokenFromRequest(request);
        final String email = JwtUtils.validateJwtAndGetSubject(signupToken); // can throw error

        final User signedUpUser = userService.signUp(
                new User(email, signUpForm.getUsername()),
                new Profile(signUpForm.getDisplayName(), signUpForm.getBio()));

        final AuthToken authToken = oAuthService.signIn(signedUpUser);
        final ResponseDto<AuthToken> responseDto = new ResponseDto<>(203, "회원가입을 완료했습니다.", authToken);
        final ResponseCookie responseCookie = WebUtils.generateCookie(authToken.getType().name(), authToken.getValue());

        return WebUtils.buildRedirectResponse("/redirect", responseCookie, HttpStatus.CREATED, responseDto);
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
