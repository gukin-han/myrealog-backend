package com.example.myrealog.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OAuthProvider {
    GOOGLE("google",
            "https://accounts.google.com/o/oauth2/v2/auth",
            "https://oauth2.googleapis.com/token",
            "1093181076891-hb8imppppmtd0gj52hqnu3ed9dt3j5ka.apps.googleusercontent.com",
            System.getenv("SECRETE_KEY_GOOGLE"),
            "http://localhost:8080/api/v1/signin/oauth/callback/google",
            "code",
            "https://www.googleapis.com/auth/userinfo.email",
            "authorization_code",
            "https://www.googleapis.com/oauth2/v2/userinfo") {

        @Override
        public String getOAuthLoginUrl() {
            return getAuthorizationUri() +
                    "?scope=" + getScope() +
                    "&response_type=" + getResponseType() +
                    "&redirect_uri=" + getRedirectUri() +
                    "&client_id=" + getClientId();
        }
    };

    private final String name;
    private final String authorizationUri;
    private final String tokenUri;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String responseType;
    private final String scope;
    private final String grantType;
    private final String resourceUri;

    abstract public String getOAuthLoginUrl();
}
