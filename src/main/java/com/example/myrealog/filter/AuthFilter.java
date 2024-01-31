package com.example.myrealog.filter;

import com.example.myrealog.auth.OAuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class AuthFilter implements Filter {


    private final OAuthService oAuthService;

    private static final String[] whiteList = {
            "/api/v1/signin/oauth/google",
            "/api/v1/signin/oauth/callback/google"};

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            if (isAuthRequiredPath(requestURI)) {
                log.info("AUTHORIZATION START [{}][{}]", uuid, requestURI);

                final String accessToken = httpRequest.getHeader("Authorization").substring(7);
                final String userId = oAuthService.validateTokenAndGetSubject(accessToken);
                request.setAttribute("userId", userId);

                log.info("AUTHORIZATION SUCCESS [{}][{}]", uuid, requestURI);
            }

            chain.doFilter(request, response);
        } catch (JwtException err) {
            log.error("AUTHORIZATION ERROR [{}][{}]", uuid, requestURI);
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    private boolean isAuthRequiredPath(String requestURI) {
        return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
    }
}
