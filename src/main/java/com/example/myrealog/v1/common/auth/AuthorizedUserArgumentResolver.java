package com.example.myrealog.v1.common.auth;

import com.example.myrealog.v1.common.exception.InvalidTokenException;
import com.example.myrealog.v1.common.utils.JwtUtils;
import com.example.myrealog.v1.common.utils.WebUtils;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
@RequiredArgsConstructor
public class AuthorizedUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        final boolean hasAuthorizedAnnotation = parameter.hasParameterAnnotation(Authorized.class);
        final boolean hasStringType = UserPrincipal.class.isAssignableFrom(parameter.getParameterType());

        return hasStringType && hasAuthorizedAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String uuid = (String) request.getAttribute("uuid");
        final String requestURI = request.getRequestURI();

        try {
            log.info("AUTHORIZATION START [{}][{}]", uuid, requestURI);
            final String accessToken = WebUtils.extractTokenFromRequest(request);
            final String userId = JwtUtils.validateJwtAndGetSubject(accessToken);
            final UserPrincipal userPrincipal = new UserPrincipal(Long.parseLong(userId));
            log.info("AUTHORIZATION SUCCESS [{}][{}]", uuid, requestURI);
            return userPrincipal;

        } catch (JwtException err) {
            log.error("AUTHORIZATION FAIL [{}][{}]", uuid, requestURI);
            throw new InvalidTokenException();
        }
    }

}
