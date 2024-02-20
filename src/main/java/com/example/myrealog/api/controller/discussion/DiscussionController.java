package com.example.myrealog.api.controller.discussion;

import com.example.myrealog.api.ApiResponse;
import com.example.myrealog.api.controller.discussion.request.DiscussionCreateRequest;
import com.example.myrealog.api.controller.discussion.request.DiscussionUpdateRequest;
import com.example.myrealog.api.service.discussion.DiscussionService;
import com.example.myrealog.api.service.discussion.response.DiscussionResponse;
import com.example.myrealog.auth.Authorized;
import com.example.myrealog.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DiscussionController {

    private final DiscussionService discussionService;

    @PostMapping("/api/v1/articles/{articleId}/discussions/new")
    public ApiResponse<DiscussionResponse> createDiscussion(@Authorized UserPrincipal principal,
                                                            @PathVariable("articleId") Long articleId,
                                                            @Valid @RequestBody DiscussionCreateRequest request) {

        return ApiResponse.ok(
                discussionService.createDiscussion(
                        principal.getUserId(),
                        articleId,
                        request.toServiceRequest()
                )
        );
    }

    @GetMapping("/api/v1/articles/{articleId}/discussions")
    public ApiResponse<List<DiscussionResponse>> getDiscussions(@PathVariable("articleId") Long articleId) {
        return ApiResponse.ok(discussionService.getDiscussions(articleId));
    }

    @PatchMapping("/api/v1/discussions/{discussionId}")
    public ApiResponse<DiscussionResponse> updateDiscussion(@Authorized UserPrincipal principal,
                                                            @PathVariable("discussionId") Long discussionId,
                                                            @Valid @RequestBody DiscussionUpdateRequest request) {

        return ApiResponse.ok(
                discussionService.updateDiscussion(
                        principal.getUserId(),
                        discussionId,
                        request.toServiceRequest()
                )
        );

    }

}
