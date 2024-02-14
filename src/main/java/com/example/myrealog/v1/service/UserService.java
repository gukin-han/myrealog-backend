package com.example.myrealog.v1.service;

import com.example.myrealog.v1.common.dto.request.UserSignupRequest;
import com.example.myrealog.v1.common.exception.UserNotFoundException;
import com.example.myrealog.v1.model.User;
import com.example.myrealog.v1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User signUp(UserSignupRequest request, String email) {
        validateDuplicateUserByEmailAndUsername(email, request.getUsername());

        final User user = User.create(email, request.getUsername(), request.getDisplayName(), request.getBio());
        return userRepository.save(user);
    }

    @Transactional
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional
    public User findUserAndProfileById(Long id) {
        final Optional<User> findUser = userRepository.findUserAndProfileById(id);
        return validateUserExistence(findUser);

    }

    private User validateUserExistence(Optional<User> optionalUser) {
        return optionalUser.orElseThrow(UserNotFoundException::new);
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
