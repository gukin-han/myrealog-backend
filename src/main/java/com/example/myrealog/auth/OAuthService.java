package com.example.myrealog.auth;

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
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

import static com.example.myrealog.auth.OAuthProvider.GOOGLE;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private static final String SECRET_KEY = "" +
            "a0daASD0as0daLKOIWN123Liqpvm211340vxlsewrLiqpvm21134" +
            "040vxlsewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxl" +
            "sewrLiqpvm2113404040vxlsewrLiqpvm2113404040vxlsewrLi" +
            ""; // 비밀 키 설정

    private static final long EXPIRATION_TIME = 86400000;
    private static final SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String getUserEmail(final String code) {
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

        return restTemplate.exchange(GOOGLE.getResourceUri(), HttpMethod.GET, request, UserAttributesDto.class)
                .getBody();
    }

    public String generateToken(String email) {

        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateTokenAndGetEmail(String token) throws JwtException {
        final Jws<Claims> claimsJws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        return claimsJws.getPayload().getSubject();
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
