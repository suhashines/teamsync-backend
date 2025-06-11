
package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.dto.feedPostDTO.*;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.FeedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/feedposts")
@RequiredArgsConstructor
public class FeedPostController {

    private final FeedPostService feedPostsService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<FeedPostResponseDTO>>> getAllFeedPosts() {
        List<FeedPostResponseDTO> feedPosts = feedPostsService.getAllFeedPosts();
        SuccessResponse<List<FeedPostResponseDTO>> resp = SuccessResponse.<List<FeedPostResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Feed posts fetched successfully")
                .data(feedPosts)
                .build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createFeedPost(
            @Valid @RequestBody FeedPostCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        feedPostsService.createFeedPost(request, userEmail);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Feed post created successfully")
//                .data(createdPost)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<FeedPostWithReactionDTO>> getFeedPostById(@PathVariable Long id) {
        FeedPostWithReactionDTO feedPost = feedPostsService.getFeedPostById(id);
        SuccessResponse<FeedPostWithReactionDTO> resp = SuccessResponse.<FeedPostWithReactionDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Feed post fetched successfully")
                .data(feedPost)
                .build();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateFeedPost(
            @PathVariable Long id,
            @Valid @RequestBody FeedPostUpdateRequest request) {
        feedPostsService.updateFeedPost(id, request);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Feed post updated successfully")
//                .data(updatedPost)
                .build();
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteFeedPost(@PathVariable Long id) {
        feedPostsService.deleteFeedPost(id);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Feed post deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
