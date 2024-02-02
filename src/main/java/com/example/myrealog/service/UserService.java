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

    @Transactional
    public User findOneByEmail(String email) throws IllegalArgumentException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Transactional
    public User findOneById(Long id) throws IllegalArgumentException {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Transactional
    public User findUserAndProfileByEmail(String email) {
        return userRepository.findUserAndProfileByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    @Transactional
    public User findUserAndProfileById(Long id) {
        return userRepository.findUserAndProfileById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }

    private void validateDuplicateUser(User user) {
        if (userRepository.existsByEmailAndUsername(user.getEmail(), user.getUsername())) {
            throw new IllegalArgumentException("중복된 유저이름 혹은 이메일입니다.");
        }
    }
}
