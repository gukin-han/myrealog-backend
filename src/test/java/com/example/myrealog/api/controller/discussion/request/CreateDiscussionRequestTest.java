package com.example.myrealog.api.controller.discussion.request;

import com.example.myrealog.api.service.discussion.request.DiscussionCreateServiceRequest;
import com.example.myrealog.domain.discussion.Discussion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CreateDiscussionRequestTest {


    @DisplayName("디스커션 생성 요청 dto가 부모가 있는 경우 서비스 요청 dto에도 부모가 있다.")
    @Test
    void toServiceRequestWithParentTest(){
        //given
        final CreateDiscussionRequest parent = CreateDiscussionRequest.builder().build();
        final CreateDiscussionRequest child = CreateDiscussionRequest.builder().parent(parent).build();

        //when
        final DiscussionCreateServiceRequest serviceRequest = child.toServiceRequest();

        //then
        assertThat(serviceRequest.getParent()).isNotNull();
    }

    @DisplayName("디스커션 생성 요청 dto가 부모가 없는 경우 서비스 요청 dto에도 부모가 없다.")
    @Test
    void toServiceRequestWithoutParentTest(){
        //given
        final CreateDiscussionRequest child = CreateDiscussionRequest.builder().parent(null).build();

        //when
        final DiscussionCreateServiceRequest serviceRequest = child.toServiceRequest();

        //then
        assertThat(serviceRequest.getParent()).isNull();
    }
}