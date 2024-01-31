package com.example.myrealog.service;

import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.ProfileRepository;
import com.example.myrealog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public User signUp(User user, Profile profile) {

        validateDuplicateUser(user);
        final Profile savedProfile = profileRepository.save(profile);
        user.updateProfile(savedProfile);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User findOneByUserId(Long id) throws IllegalArgumentException {
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 사용자 ID: {}", id);
                    return new IllegalArgumentException("존재하지 않는 유저입니다.");
                });
    }

    private void validateDuplicateUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 유저이름입니다.");
        }
    }
}
