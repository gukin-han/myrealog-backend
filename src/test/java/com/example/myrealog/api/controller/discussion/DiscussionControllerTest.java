package com.example.myrealog.api.controller.discussion;

import com.example.myrealog.api.controller.discussion.request.DiscussionCreateRequest;
import com.example.myrealog.api.controller.discussion.request.DiscussionUpdateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private ResultActions performCreateRequest(int articleId, DiscussionCreateRequest request) throws Exception {
        return mockMvc.perform(post("/api/v1/articles/{articleId}/discussions/new", articleId)
                .header("Authorization", "accessToken")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetRequest(int articleId) throws Exception {
        return mockMvc.perform(get("/api/v1/articles/{articleId}/discussions", articleId)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performUpdateRequest(int discussionId, DiscussionUpdateRequest request) throws Exception {
        return mockMvc.perform(patch("/api/v1/discussions/{discussionId}", discussionId)
                .header("Authorization", "accessToken")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performDeleteRequest(int discussionId) throws Exception {
        return mockMvc.perform(delete("/api/v1/discussions/{discussionId}", discussionId)
                .header("Authorization", "accessToken")
                .contentType(MediaType.APPLICATION_JSON));
    }

    @DisplayName("새 디스커션을 생성한다.")
    @Test
    void createDiscussionTest() throws Exception {
        // given
        final DiscussionCreateRequest request = DiscussionCreateRequest.builder()
                .content("test content")
                .build();

        // when
        final ResultActions resultActions = performCreateRequest(1, request);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("새 디스커션을 생성할때 디스커션 내용은 필수값이다.")
    @Test
    void createDiscussionWithoutContentTest() throws Exception {
        // given
        final DiscussionCreateRequest request = DiscussionCreateRequest.builder()
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
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("아티클 아이디로 모든 디스커션들을 조회한다.")
    @Test
    void getDiscussionsTest() throws Exception{
        //given
        int discussionId = 1;

        //when
        final ResultActions resultActions = performGetRequest(discussionId);

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("디스커션 내용을 업데이트 한다.")
    @Test
    void updateDiscussionTest() throws Exception {
        // given
        final DiscussionUpdateRequest request = DiscussionUpdateRequest.builder()
                .content("test content")
                .build();

        // when
        final ResultActions resultActions = performUpdateRequest(1, request);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("디스커션을 업데이트할때 디스커션 내용은 필수값이다.")
    @Test
    void updateDiscussionWithoutContentTest() throws Exception {
        // given
        final DiscussionUpdateRequest request = DiscussionUpdateRequest.builder()
                .content("")
                .build();

        // when
        final ResultActions resultActions = performUpdateRequest(1, request);

        // then
        resultActions
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("디스커션 내용은 필수입니다."))
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("디스커션 아이디로 디스커션을 삭제한다.")
    @Test
    void deleteDiscussionTest() throws Exception {
        //given
        int discussionId = 1;

        //when
        final ResultActions resultActions = performDeleteRequest(discussionId);

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("NO_CONTENT"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("204"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}