package com.example.myrealog.api.controller.discussion;

import com.example.myrealog.api.controller.discussion.request.CreateDiscussionRequest;
import com.example.myrealog.api.service.discussion.DiscussionService;
import com.example.myrealog.v1.common.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(DiscussionController.class)
class DiscussionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DiscussionService discussionService;

    private MockedStatic<JwtUtils> mockedJwtUtils;

    @BeforeEach
    void setUp() {
        mockedJwtUtils = Mockito.mockStatic(JwtUtils.class);
        mockedJwtUtils.when(() -> JwtUtils.validateJwtAndGetSubject(any()))
                .thenReturn("1");
    }

    @AfterEach
    void tearDown() {
        mockedJwtUtils.close();
    }

    private ResultActions performCreateRequest(int articleId, CreateDiscussionRequest request) throws Exception {
        return mockMvc.perform(post("/api/v1/articles/{id}/discussions/new", articleId)
                .header("Authorization", "accessToken")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("새 디스커션을 생성한다.")
    @Test
    void createDiscussionTest() throws Exception {
        // given
        final CreateDiscussionRequest request = CreateDiscussionRequest.builder()
                .content("test content")
                .build();

        // when
        final ResultActions resultActions = performCreateRequest(1, request);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk());
    }

    @DisplayName("새 디스커션을 생성할때 디스커션 내용은 필수값이다.")
    @Test
    void createDiscussionWithoutContentTest() throws Exception {
        // given
        final CreateDiscussionRequest request = CreateDiscussionRequest.builder()
                .content("")
                .build();

        // when
        final ResultActions resultActions = performCreateRequest(1, request);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("디스커션 내용은 필수입니다."))
                .andExpect(jsonPath("$.success").value("false"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}