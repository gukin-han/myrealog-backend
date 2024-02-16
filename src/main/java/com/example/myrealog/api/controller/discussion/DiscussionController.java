package com.example.myrealog.api.controller.discussion;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.api.controller.discussion.request.CreateDiscussionRequest;
import com.example.myrealog.api.service.discussion.DiscussionService;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DiscussionController {

    private final DiscussionService discussionService;

    @PostMapping("/api/v1/articles/{id}/discussions/new")
    public ApiResponse<DiscussionResponse> createDiscussion(@Authorized UserPrincipal principal,
                                                            @PathVariable("id") Long articleId,
                                                            @Valid @RequestBody CreateDiscussionRequest request) {

        return ApiResponse.ok(
                discussionService.createDiscussion(
                        principal.getUserId(),
                        articleId,
                        request.toServiceRequest()
                )
        );
    }

    @GetMapping("/api/v1/articles/{id}/discussions")
    public ApiResponse<List<DiscussionResponse>> getDiscussions(@PathVariable("id") Long articleId) {
        return ApiResponse.ok(discussionService.getDiscussions(articleId));
    }

}
