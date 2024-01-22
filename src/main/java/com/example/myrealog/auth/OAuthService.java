package com.example.myrealog.auth;

import com.example.myrealog.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

import static com.example.myrealog.auth.OAuthProvider.GOOGLE;

@Service
@RequiredArgsConstructor
@Transactional
public class OAuthService {

    private static final String SECRET_KEY = "a0daASD0as0daLKOIWN123Liqpvm211340vxlsewrLiqpvm211340"; // 비밀 키 설정
    private static final long EXPIRATION_TIME = 86400000;

    public String getUserEmail(final String code) {
        final String accessToken = getAccessToken(code).getAccessToken();
        return getUserAttributes(accessToken).getEmail();
    }

    private OAuthTokenResponse getAccessToken(final String code) {
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

        return restTemplate.postForEntity(GOOGLE.getTokenUri(), request, OAuthTokenResponse.class)
                .getBody();
    }

    private UserAttributesResponse getUserAttributes(String accessToken) {
        final RestTemplate restTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        final HttpEntity request = new HttpEntity(headers);

        return restTemplate.exchange(GOOGLE.getResourceUri(), HttpMethod.GET, request, UserAttributesResponse.class)
                .getBody();
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

}
