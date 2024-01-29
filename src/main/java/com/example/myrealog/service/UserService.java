package com.example.myrealog.service;

import com.example.myrealog.model.Profile;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.ProfileRepository;
import com.example.myrealog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    @Transactional
    public User signUp(User user, Profile profile) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 유저이름입니다.");
        }
        final Profile savedProfile = profileRepository.save(profile);
        user.updateProfile(savedProfile);
        return userRepository.save(user);
    }
}
