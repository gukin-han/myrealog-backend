package com.example.myrealog.auth;

import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.example.myrealog.auth.OAuthProvider.GOOGLE;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private final UserRepository userRepository;

    private static final long EXPIRATION_TIME = 86400000;
    private static final String SECRET_KEY = "a0daASD0as0daLKOIWN123Liqpvm211340vxlsewrLiqpvm21134040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLi"; // 비밀 키 설정
    private static final int SEVEN_DAYS = 7 * 24 * 60 * 60;
    private static final String REDIRECT_URL_BASE = "http://localhost:3000";
    private static final SecretKey SECRET = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public ResponseEntity<Void> signIn(String code) {
        final String userEmail = getUserEmailFromOAuthServer(code);
        final Optional<User> findUser = userRepository.findUserByEmail(userEmail);

        return signIn(findUser, userEmail, HttpStatus.FOUND);
    }

    public ResponseEntity<Void> signIn(User signedUpUser) {
        return signIn(Optional.of(signedUpUser), null, HttpStatus.CREATED);
    }

    private ResponseEntity<Void> signIn(Optional<User> optionalUser,
                                        String userEmail,
                                        HttpStatus status) {

        String redirectUrl = REDIRECT_URL_BASE;
        ResponseCookie cookie;

        if (optionalUser.isEmpty()) {
            redirectUrl += "/signup?email=" + userEmail;
            cookie = generateCookie("signupToken", generateToken(userEmail));
        } else {
            redirectUrl += "/redirect";
            final String userId = String.valueOf(optionalUser.get().getId());
            cookie = generateCookie("accessToken", generateToken(userId));
        }

        return buildRedirectResponse(redirectUrl, cookie, status);
    }

    private ResponseEntity<Void> buildRedirectResponse(String redirectUrl, ResponseCookie cookie, HttpStatus status) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(redirectUrl));
        headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.status(status).headers(headers).build();
    }

    private static ResponseCookie generateCookie(String name, String value) {
        return ResponseCookie.from(name, value)
                .maxAge(SEVEN_DAYS)
                .httpOnly(true)
//                .secure(true) // HTTPS 환경에서 사용
                .path("/")
                .build();
    }


    public String getUserEmailFromOAuthServer(final String code) {
        final String accessToken = getAccessToken(code).getAccessToken();
        return getUserAttributes(accessToken).getEmail();
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

    private UserAttributesDto getUserAttributes(String accessToken) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final HttpEntity request = new HttpEntity(headers);

        return restTemplate
                .exchange(GOOGLE.getResourceUri()
                        , HttpMethod.GET
                        , request
                        , UserAttributesDto.class)
                .getBody();
    }

    public String generateToken(String email) {
        return Jwts
                .builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateTokenAndGetSubject(String token) throws JwtException {
        final Jws<Claims> claimsJws = Jwts
                .parser()
                .verifyWith(SECRET) // error 발생 지점
                .build()
                .parseSignedClaims(token);

        return claimsJws
                .getPayload()
                .getSubject();
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
