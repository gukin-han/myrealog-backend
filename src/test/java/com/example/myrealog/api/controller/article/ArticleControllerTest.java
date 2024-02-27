package com.example.myrealog.api.controller.article;

import com.example.myrealog.api.controller.discussion.DiscussionController;
import com.example.myrealog.api.controller.discussion.request.DiscussionCreateRequest;
import com.example.myrealog.api.service.article.ArticleService;
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

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ArticleService articleService;

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

    private ResultActions performCreateRequest() throws Exception {
        return mockMvc.perform(post("/api/v1/articles/draft")
                .header("Authorization", "accessToken")
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performGetRequest(String username, String slug) throws Exception {
        return mockMvc.perform(get("/api/v1/articles/{username}/{slug}", username, slug)
                .contentType(MediaType.APPLICATION_JSON));
    }


    @DisplayName("아티클 초안을 생성한다.")
    @Test
    void draftCreateControllerTest() throws Exception {
        //given
        //when
        final ResultActions resultActions = performCreateRequest();

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("아티클 슬러그와 유저이름을 받아 아티클을 반환한다.")
    @Test
    void getOneBySlugAndUsernameTest() throws Exception {
        //given
        //when
        final ResultActions resultActions = performGetRequest("usernameTest", "slugTest");

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @DisplayName("최근 게시글을 모두 불러온다.")
    @Test
    void getAllRecentArticlesOrderByCreatedDateTest() throws Exception {
        //given
        //when
        final ResultActions resultActions = mockMvc.perform(get("/api/v1/articles/recent")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @DisplayName("초안 아티클을 공개로 게시한다.")
    @Test
    void makeDraftPublishTest() throws Exception {
        //given
        final int articleId = 1;

        //when
        final ResultActions resultActions = mockMvc.perform(patch("/api/v1/articles/{articleId}/publish", articleId)
                .header("Authorization", "accessToken")
                .contentType(MediaType.APPLICATION_JSON));

        //then
        resultActions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.httpStatus").value("OK"))
                .andExpect(jsonPath("$.message").value(""))
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
}