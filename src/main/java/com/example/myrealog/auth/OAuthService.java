package com.example.myrealog.auth;

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

import static com.example.myrealog.auth.AuthToken.Type.ACCESS_TOKEN;
import static com.example.myrealog.auth.OAuthProvider.GOOGLE;
import static com.example.myrealog.utils.JwtUtils.generateJwt;
import static com.example.myrealog.utils.WebUtils.buildRedirectResponse;
import static com.example.myrealog.utils.WebUtils.generateCookie;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private final UserRepository userRepository;

    private static final String REDIRECT_URL_BASE = "http://localhost:3000";

    public AuthToken signIn(User user) {
        final String accessToken = generateJwt(user.getId().toString());
        return new AuthToken(ACCESS_TOKEN, accessToken);
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

        String redirectUrl = REDIRECT_URL_BASE;
        ResponseCookie cookie;

        if (optionalUser.isEmpty()) {
            redirectUrl += "/signup?email=" + userEmail;
            cookie = generateCookie("signupToken", generateJwt(userEmail));
        } else {
            redirectUrl += "/redirect";
            cookie = generateCookie("accessToken", generateJwt(optionalUser.get().getId().toString()));
        }

        return buildRedirectResponse(redirectUrl, cookie, status);
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

        parameters.add("client_id", GOOGLE.getClientId());
        parameters.add("client_secret", GOOGLE.getClientSecret());
        parameters.add("code", code);
        parameters.add("redirect_uri", GOOGLE.getRedirectUri());
        parameters.add("grant_type", GOOGLE.getGrantType());

        final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        return restTemplate.postForEntity(GOOGLE.getTokenUri(), request, OAuthTokenDto.class)
                .getBody();
    }

    private UserAttributesDto getUserResources(String accessToken) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final HttpEntity<?> request = new HttpEntity<>(headers);

        return restTemplate.exchange(GOOGLE.getResourceUri(), HttpMethod.GET, request, UserAttributesDto.class)
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
