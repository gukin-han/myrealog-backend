package com.example.myrealog.api.service.discussion.request;

import com.example.myrealog.domain.discussion.Discussion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DiscussionCreateServiceRequestTest {

    @DisplayName("디스커션 부모가 있는 경우 부모 depth 보다 1 큰 값을 depth 로 할당한다.")
    @Test
    void discussionWithParentShouldGetOnePlusParentDepth(){
        //given
        final int parentDepth = 0;
        final DiscussionCreateServiceRequest parent = DiscussionCreateServiceRequest.builder().depth(parentDepth).build();
        final DiscussionCreateServiceRequest child = DiscussionCreateServiceRequest.builder().parent(parent).build();

        //when
        final Discussion discussion = child.toEntity(null, null);

        //then
        assertThat(discussion.getDepth()).isEqualTo(parentDepth + 1);
        assertThat(discussion.getParent()).isNotNull();
    }

    @DisplayName("디스커션 부모가 없는 경우 부모 depth 보다 0을 depth 로 할당한다.")
    @Test
    void discussionWithoutParentShouldGetZeroDepth(){
        //given
        final DiscussionCreateServiceRequest child = DiscussionCreateServiceRequest.builder().parent(null).build();

        //when
        final Discussion discussion = child.toEntity(null, null);

        //then
        assertThat(discussion.getDepth()).isEqualTo(0);
        assertThat(discussion.getParent()).isNull();
    }
}