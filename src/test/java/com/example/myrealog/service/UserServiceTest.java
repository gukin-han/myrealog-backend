package com.example.myrealog.service;

import com.example.myrealog.common.dto.request.UserSignupRequest;
import com.example.myrealog.common.exception.UserNotFoundException;
import com.example.myrealog.model.User;
import com.example.myrealog.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @DisplayName("유저 정보를 받아 유저를 회원가입한다.")
    @Test
    void signupTest(){
        //given
        final UserSignupRequest userSignupRequest = createUserSignupRequest("username1", "displayName1", "bio1");
        final String email ="test@test.com";

        //when
        final User signupUser = userService.signUp(userSignupRequest, email);

        //then
        assertThat(signupUser.getId()).isNotNull();
        assertThat(signupUser.getProfile().getId()).isNotNull();
        assertThat(signupUser)
                .extracting("username", "email")
                .contains("username1", "test@test.com");
        assertThat(signupUser.getProfile())
                .extracting("displayName", "bio")
                .contains("displayName1", "bio1");
    }


    @DisplayName("중복된 이메일로 회원가입시 에러를 던진다.")
    @Test
    void signupUserWithDuplicateEmailThrowErrorTest(){
        //given
        final String duplicateEmail ="test@test.com";
        final UserSignupRequest userSignupRequest = createUserSignupRequest("username1", "displayName1", "bio1");
        userService.signUp(userSignupRequest, duplicateEmail);

        //when
        final UserSignupRequest userSignupRequest2 = createUserSignupRequest("username2", "displayName2", "bio2");

        //then
        assertThatThrownBy(() -> userService.signUp(userSignupRequest2, duplicateEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 가입된 이메일입니다.");
    }

    @DisplayName("중복된 유저이름으로 회원가입시 에러를 던진다.")
    @Test
    void signupUserWithDuplicateUsernameThrowErrorTest(){
        //given
        final String duplicateUsername = "username1";

        final UserSignupRequest userSignupRequest = createUserSignupRequest(duplicateUsername, "displayName1", "bio1");
        final String email1 = "test1@test.com";
        userService.signUp(userSignupRequest, email1);

        //when
        final UserSignupRequest userSignupRequest2 = createUserSignupRequest(duplicateUsername, "displayName2", "bio2");
        final String email2 = "test2@test.com";

        //then
        assertThatThrownBy(() -> userService.signUp(userSignupRequest2, email2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 유저이름입니다. 다시 입력해주세요.");
    }

    @DisplayName("유저 아이디로 유저를 조회할 수 있다.")
    @Test
    void findUserByIdTest(){
        //given
        final String username1 = "username1";
        final UserSignupRequest userSignupRequest = createUserSignupRequest(username1, "displayName1", "bio1");
        final String email1 = "test1@test.com";

        final String username2 = "username2";
        final UserSignupRequest userSignupRequest2 = createUserSignupRequest(username2, "displayName1", "bio1");
        final String email2 = "test2@test.com";

        final User signupUser1 = userService.signUp(userSignupRequest, email1);
        final User signupUser2 = userService.signUp(userSignupRequest2, email2);

        //when
        final User findUser = userService.findById(signupUser1.getId());

        //then
        assertThat(findUser.getId()).isEqualTo(signupUser1.getId());
        assertThat(findUser.getId()).isNotEqualTo(signupUser2.getId());
    }

    @DisplayName("존재하지 않는 유저 아이디로 유저를 조회하면 에러를 던진다.")
    @Test
    void findUserByNotExistIdThrowErrorTest(){
        //given
        final String username1 = "username1";
        final UserSignupRequest userSignupRequest = createUserSignupRequest(username1, "displayName1", "bio1");
        final String email1 = "test1@test.com";
        final User signupUser1 = userService.signUp(userSignupRequest, email1);

        //when
        //then
        assertThatThrownBy(() -> userService.findById(signupUser1.getId() + 1))
                .isInstanceOf(UserNotFoundException.class);
    }

    private static UserSignupRequest createUserSignupRequest(String username, String displayName, String bio) {
        return UserSignupRequest.builder()
                .username(username)
                .displayName(displayName)
                .bio(bio)
                .build();
    }
}