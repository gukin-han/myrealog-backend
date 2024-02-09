package com.example.myrealog.common.auth;

import com.example.myrealog.common.dto.response.AuthTokenResponse;
import com.example.myrealog.common.dto.response.ResponseWrapper;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static com.example.myrealog.common.dto.response.AuthTokenResponse.Type.ACCESS_TOKEN;
import static com.example.myrealog.common.dto.response.AuthTokenResponse.Type.SIGNUP_TOKEN;
import static com.example.myrealog.common.utils.JwtUtils.generateJwt;
import static com.example.myrealog.common.utils.WebUtils.buildRedirectResponse;
import static com.example.myrealog.common.utils.WebUtils.generateCookie;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private final UserRepository userRepository;

    public AuthTokenResponse signIn(User user) {
        final String accessToken = generateJwt(user.getId().toString());
        return AuthTokenResponse.of(ACCESS_TOKEN, accessToken);
    }

    public ResponseEntity<?> signInOrGetSignUpToken(String code) {
        final String userEmail = getUserInfo(code);
        final Optional<User> findUser = userRepository.findUserByEmail(userEmail);

        return signInOrGetSignUpToken(findUser, userEmail, HttpStatus.FOUND);
    }

    public ResponseEntity<?> signInOrGetSignUpToken(User signedUpUser) {
        return signInOrGetSignUpToken(Optional.of(signedUpUser), null, HttpStatus.CREATED);
    }

    private ResponseEntity<?> signInOrGetSignUpToken(Optional<User> optionalUser,
                                                        String userEmail,
                                                        HttpStatus status) {

        String redirectUrl = "";
        ResponseCookie cookie;
        AuthTokenResponse authTokenResponse;

        if (optionalUser.isEmpty()) {
            redirectUrl += "/signup?email=" + userEmail;
            final String jwt = generateJwt(userEmail);
            cookie = generateCookie("signupToken", jwt);
            authTokenResponse = AuthTokenResponse.of(SIGNUP_TOKEN, jwt);
        } else {
            redirectUrl += "/redirect";
            final String jwt = generateJwt(optionalUser.get().getId().toString());
            cookie = generateCookie("accessToken", jwt);
            authTokenResponse = AuthTokenResponse.of(ACCESS_TOKEN, jwt);
        }

        final ResponseWrapper<AuthTokenResponse> response = ResponseWrapper.of(authTokenResponse);

        return buildRedirectResponse(redirectUrl, cookie, status, response);
    }

    public String getUserInfo(final String code) {
        final String accessToken = getAccessToken(code).getAccessToken();
        final UserAttributesDto userResources = getUserResources(accessToken);
        return userResources.getEmail();
    }

    private OAuthTokenDto getAccessToken(final String code) {
        final RestTemplate restTemplate = new RestTemplate();

        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("client_id", OAuthProvider.GOOGLE.getClientId());
        parameters.add("client_secret", OAuthProvider.GOOGLE.getClientSecret());
        parameters.add("code", code);
        parameters.add("redirect_uri", OAuthProvider.GOOGLE.getRedirectUri());
        parameters.add("grant_type", OAuthProvider.GOOGLE.getGrantType());

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        return restTemplate.postForEntity(OAuthProvider.GOOGLE.getTokenUri(), request, OAuthTokenDto.class)
                .getBody();
    }

    private UserAttributesDto getUserResources(String accessToken) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(OAuthProvider.GOOGLE.getResourceUri(), HttpMethod.GET, request, UserAttributesDto.class)
                .getBody();
    }

    @Data
    static class OAuthTokenDto {

        @JsonProperty("access_token")
        private String accessToken;

        @JsonProperty("expires_in")
        private String expiresIn;
        private String scope;

        @JsonProperty("token_type")
        private String tokenType;

        @JsonProperty("id_token")
        private String idToken;

    }

    @Data
    static class UserAttributesDto {

        private String email;

    }

}
