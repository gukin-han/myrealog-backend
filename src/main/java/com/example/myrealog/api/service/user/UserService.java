package com.example.myrealog.api.service.user;

import com.example.myrealog.api.service.user.response.UserResponse;
import com.example.myrealog.v1.common.dto.request.UserSignUpRequest;
import com.example.myrealog.v1.common.exception.UserNotFoundException;
import com.example.myrealog.domain.user.User;
import com.example.myrealog.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User signUp(UserSignUpRequest request, String email, LocalDateTime now) {
        validateDuplicateUserByEmailAndUsername(email, request.getUsername());
        return userRepository.save(User.of(email, request.getUsername(), request.getDisplayName(), request.getBio(), now));
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public UserResponse getMe(Long id) {
        final User user = userRepository.findUserAndProfileById(id).orElseThrow(UserNotFoundException::new);
        return UserResponse.of(user);
    }

    private void validateDuplicateUserByEmailAndUsername(String email, String username) {

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("중복된 유저이름입니다. 다시 입력해주세요.");
        }
    }
}
