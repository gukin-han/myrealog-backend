package com.example.myrealog.auth;

import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
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

    private final OAuthService oAuthService;
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        final boolean hasAuthorizedAnnotation = parameter.hasParameterAnnotation(Authorized.class);
        final boolean hasUserType = User.class.isAssignableFrom(parameter.getParameterType());

        return hasUserType && hasAuthorizedAnnotation;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {

        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final String uuid = (String) request.getAttribute("uuid");
        final String requestURI = request.getRequestURI();

        try {
            log.info("AUTHORIZATION START [{}][{}]", uuid, requestURI);
            final String accessToken = request.getHeader("Authorization").substring(7);
            final String userId = oAuthService.validateTokenAndGetSubject(accessToken);


            final User user = userRepository.findUserAndProfileByUserId(Long.parseLong(userId))
                    .orElseThrow(() -> {
                        log.error("AUTHORIZATION ERROR [{}][{}]", uuid, requestURI);
                        return new UnauthorizedAccessException();
                    });

            log.info("AUTHORIZATION SUCCESS [{}][{}]", uuid, requestURI);
            return user;

        } catch (JwtException err) {
            log.error("AUTHORIZATION ERROR [{}][{}]", uuid, requestURI);
            throw new UnauthorizedAccessException();
        }
    }
}
