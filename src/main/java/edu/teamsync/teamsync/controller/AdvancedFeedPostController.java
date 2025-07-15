// package edu.teamsync.teamsync.controller;

// import edu.teamsync.teamsync.entity.FeedPosts;
// import edu.teamsync.teamsync.entity.Reactions;
// import edu.teamsync.teamsync.response.SuccessResponse;
// import edu.teamsync.teamsync.service.AdvancedFeedPostService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.format.annotation.DateTimeFormat;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDate;
// import java.time.ZonedDateTime;
// import java.util.List;

// @RestController
// @RequestMapping("/api/v1/advanced/feedposts")
// @RequiredArgsConstructor
// public class AdvancedFeedPostController {

//     private final AdvancedFeedPostService advancedFeedPostService;

//     /**
//      * Example 1: Multi-criteria search with dynamic predicates
//      */
//     @GetMapping("/search")
//     public SuccessResponse<List<FeedPosts>> searchPostsByCriteria(
//             @RequestParam(required = false) FeedPosts.FeedPostType type,
//             @RequestParam(required = false) Long authorId,
//             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startDate,
//             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endDate,
//             @RequestParam(required = false) String contentKeyword,
//             @RequestParam(defaultValue = "false") boolean isAiGenerated) {

//         List<FeedPosts> posts = advancedFeedPostService.findPostsByAdvancedCriteria(
//                 type, authorId, startDate, endDate, contentKeyword, isAiGenerated);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Posts found with advanced criteria")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 2: Posts with high engagement
//      */
//     @GetMapping("/high-engagement")
//     public SuccessResponse<List<FeedPosts>> findHighEngagementPosts(
//             @RequestParam(defaultValue = "5") int minReactions,
//             @RequestParam(defaultValue = "3") int minComments) {

//         List<FeedPosts> posts = advancedFeedPostService.findHighEngagementPosts(minReactions, minComments);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("High engagement posts found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 3: Posts by users with specific reaction patterns
//      */
//     @GetMapping("/user-reaction-pattern")
//     public SuccessResponse<List<FeedPosts>> findPostsByUserReactionPattern(
//             @RequestParam Reactions.ReactionType reactionType,
//             @RequestParam(defaultValue = "3") int minReactionCount) {

//         List<FeedPosts> posts = advancedFeedPostService.findPostsByUserReactionPattern(reactionType, minReactionCount);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Posts found by user reaction pattern")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 4: Poll posts with dominant voting option
//      */
//     @GetMapping("/poll-dominant-option")
//     public SuccessResponse<List<FeedPosts>> findPollPostsWithDominantOption(
//             @RequestParam String dominantOption) {

//         List<FeedPosts> posts = advancedFeedPostService.findPollPostsWithDominantOption(dominantOption);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Poll posts with dominant option found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 5: Posts with appreciation from specific users
//      */
//     @GetMapping("/appreciation-from-users")
//     public SuccessResponse<List<FeedPosts>> findPostsWithAppreciationFromUsers(
//             @RequestParam List<Long> fromUserIds) {

//         List<FeedPosts> posts = advancedFeedPostService.findPostsWithAppreciationFromUsers(fromUserIds);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Posts with appreciation from specified users found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 6: Posts with upcoming events
//      */
//     @GetMapping("/upcoming-events")
//     public SuccessResponse<List<FeedPosts>> findPostsWithUpcomingEvents(
//             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

//         List<FeedPosts> posts = advancedFeedPostService.findPostsWithUpcomingEvents(startDate, endDate);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Posts with upcoming events found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 7: Media posts with specific user reactions
//      */
//     @GetMapping("/media-with-user-reactions")
//     public SuccessResponse<List<FeedPosts>> findMediaPostsWithUserReactions(
//             @RequestParam List<Long> userIds,
//             @RequestParam Reactions.ReactionType reactionType) {

//         List<FeedPosts> posts = advancedFeedPostService.findMediaPostsWithUserReactions(userIds, reactionType);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Media posts with user reactions found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 8: Birthday posts with participants
//      */
//     @GetMapping("/birthday-with-participants")
//     public SuccessResponse<List<FeedPosts>> findBirthdayPostsWithParticipants(
//             @RequestParam List<Long> participantIds) {

//         List<FeedPosts> posts = advancedFeedPostService.findBirthdayPostsWithParticipants(participantIds);

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Birthday posts with participants found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 9: Posts with nested comments
//      */
//     @GetMapping("/with-nested-comments")
//     public SuccessResponse<List<FeedPosts>> findPostsWithNestedComments() {

//         List<FeedPosts> posts = advancedFeedPostService.findPostsWithNestedComments();

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Posts with nested comments found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 10: AI-generated posts with positive feedback
//      */
//     @GetMapping("/ai-positive-feedback")
//     public SuccessResponse<List<FeedPosts>> findAiGeneratedPostsWithPositiveFeedback() {

//         List<FeedPosts> posts = advancedFeedPostService.findAiGeneratedPostsWithPositiveFeedback();

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("AI-generated posts with positive feedback found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 11: Posts with engagement score and pagination
//      */
//     @GetMapping("/with-engagement-score")
//     public SuccessResponse<Page<FeedPosts>> findPostsWithEngagementScore(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {

//         Page<FeedPosts> posts = advancedFeedPostService.findPostsWithEngagementScore(
//                 PageRequest.of(page, size));

//         return SuccessResponse.<Page<FeedPosts>>builder()
//                 .message("Posts with engagement score found")
//                 .data(posts)
//                 .build();
//     }

//     /**
//      * Example 12: Posts by authors with events and appreciations
//      */
//     @GetMapping("/authors-with-events-appreciations")
//     public SuccessResponse<List<FeedPosts>> findPostsByAuthorWithEventsAndAppreciations() {

//         List<FeedPosts> posts = advancedFeedPostService.findPostsByAuthorWithEventsAndAppreciations();

//         return SuccessResponse.<List<FeedPosts>>builder()
//                 .message("Posts by authors with events and appreciations found")
//                 .data(posts)
//                 .build();
//     }
// } 