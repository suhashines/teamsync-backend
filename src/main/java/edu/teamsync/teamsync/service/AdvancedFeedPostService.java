// package edu.teamsync.teamsync.service;

// import edu.teamsync.teamsync.entity.*;
// import edu.teamsync.teamsync.repository.*;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.jpa.domain.Specification;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.time.LocalDate;
// import java.time.ZonedDateTime;
// import java.util.List;
// import java.util.Optional;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// @Transactional
// public class AdvancedFeedPostService {

//     private final FeedPostRepository feedPostRepository;
//     private final ReactionRepository reactionRepository;
//     private final CommentRepository commentRepository;
//     private final EventRepository eventRepository;
//     private final PollVoteRepository pollVoteRepository;
//     private final AppreciationRepository appreciationRepository;
//     private final UserRepository userRepository;

//     // ==================== COMPLEX QUERY EXAMPLES ====================

//     /**
//      * Example 1: Multi-criteria search with dynamic predicates
//      * Find posts by type, author, date range, and content keywords
//      */
//     public List<FeedPosts> findPostsByAdvancedCriteria(
//             FeedPosts.FeedPostType type,
//             Long authorId,
//             ZonedDateTime startDate,
//             ZonedDateTime endDate,
//             String contentKeyword,
//             boolean isAiGenerated) {

//         Specification<FeedPosts> spec = Specification.where(null);

//         // Add type filter
//         if (type != null) {
//             spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
//         }

//         // Add author filter
//         if (authorId != null) {
//             spec = spec.and((root, query, cb) -> cb.equal(root.get("author").get("id"), authorId));
//         }

//         // Add date range filter
//         if (startDate != null && endDate != null) {
//             spec = spec.and((root, query, cb) -> 
//                 cb.between(root.get("createdAt"), startDate, endDate));
//         } else if (startDate != null) {
//             spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), startDate));
//         } else if (endDate != null) {
//             spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), endDate));
//         }

//         // Add content keyword filter (case-insensitive)
//         if (contentKeyword != null && !contentKeyword.trim().isEmpty()) {
//             spec = spec.and((root, query, cb) -> 
//                 cb.like(cb.lower(root.get("content")), "%" + contentKeyword.toLowerCase() + "%"));
//         }

//         // Add AI generation filter
//         spec = spec.and((root, query, cb) -> cb.equal(root.get("isAiGenerated"), isAiGenerated));

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 2: Posts with high engagement (reactions + comments)
//      * Find posts that have more than X reactions OR more than Y comments
//      */
//     public List<FeedPosts> findHighEngagementPosts(int minReactions, int minComments) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Subquery for reactions count
//             var reactionSubquery = query.subquery(Long.class);
//             var reactionRoot = reactionSubquery.from(Reactions.class);
//             reactionSubquery.select(cb.count(reactionRoot))
//                     .where(cb.equal(reactionRoot.get("post"), root));

//             // Subquery for comments count
//             var commentSubquery = query.subquery(Long.class);
//             var commentRoot = commentSubquery.from(Comments.class);
//             commentSubquery.select(cb.count(commentRoot))
//                     .where(cb.equal(commentRoot.get("post"), root));

//             // Combine conditions with OR
//             return cb.or(
//                 cb.greaterThan(reactionSubquery, (long) minReactions),
//                 cb.greaterThan(commentSubquery, (long) minComments)
//             );
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 3: Posts by users with specific reaction patterns
//      * Find posts from users who frequently use specific reaction types
//      */
//     public List<FeedPosts> findPostsByUserReactionPattern(Reactions.ReactionType reactionType, int minReactionCount) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Subquery to find users with specific reaction pattern
//             var userSubquery = query.subquery(Long.class);
//             var reactionRoot = userSubquery.from(Reactions.class);
//             userSubquery.select(reactionRoot.get("user").get("id"))
//                     .where(cb.and(
//                         cb.equal(reactionRoot.get("reactionType"), reactionType),
//                         cb.equal(reactionRoot.get("user"), root.get("author"))
//                     ))
//                     .groupBy(reactionRoot.get("user").get("id"))
//                     .having(cb.greaterThan(cb.count(reactionRoot), (long) minReactionCount));

//             return cb.in(root.get("author").get("id")).value(userSubquery);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 4: Posts with specific poll voting patterns
//      * Find poll posts where a specific option has the most votes
//      */
//     public List<FeedPosts> findPollPostsWithDominantOption(String dominantOption) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Ensure it's a poll post
//             var pollTypeCondition = cb.equal(root.get("type"), FeedPosts.FeedPostType.poll);

//             // Subquery to count votes for the dominant option
//             var dominantVotesSubquery = query.subquery(Long.class);
//             var pollVoteRoot = dominantVotesSubquery.from(PollVotes.class);
//             dominantVotesSubquery.select(cb.count(pollVoteRoot))
//                     .where(cb.and(
//                         cb.equal(pollVoteRoot.get("poll"), root),
//                         cb.equal(pollVoteRoot.get("selectedOption"), dominantOption)
//                     ));

//             // Subquery to count total votes
//             var totalVotesSubquery = query.subquery(Long.class);
//             var totalVoteRoot = totalVotesSubquery.from(PollVotes.class);
//             totalVotesSubquery.select(cb.count(totalVoteRoot))
//                     .where(cb.equal(totalVoteRoot.get("poll"), root));

//             // Condition: dominant option votes > 50% of total votes
//             var dominantCondition = cb.greaterThan(
//                 cb.quot(dominantVotesSubquery, totalVotesSubquery),
//                 cb.literal(0.5)
//             );

//             return cb.and(pollTypeCondition, dominantCondition);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 5: Posts with complex appreciation patterns
//      * Find posts where the author has received appreciations from specific users
//      */
//     public List<FeedPosts> findPostsWithAppreciationFromUsers(List<Long> fromUserIds) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Subquery to find appreciations for this post's author
//             var appreciationSubquery = query.subquery(Long.class);
//             var appreciationRoot = appreciationSubquery.from(Appreciations.class);
//             appreciationSubquery.select(cb.count(appreciationRoot))
//                     .where(cb.and(
//                         cb.equal(appreciationRoot.get("toUser"), root.get("author")),
//                         cb.in(appreciationRoot.get("fromUser").get("id")).value(fromUserIds)
//                     ));

//             return cb.greaterThan(appreciationSubquery, 0L);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 6: Posts with upcoming events
//      * Find posts that have associated events within a date range
//      */
//     public List<FeedPosts> findPostsWithUpcomingEvents(LocalDate startDate, LocalDate endDate) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Join with Events entity
//             var eventJoin = root.join("events", jakarta.persistence.criteria.JoinType.INNER);
            
//             return cb.and(
//                 cb.between(eventJoin.get("date"), startDate, endDate),
//                 cb.equal(root.get("type"), FeedPosts.FeedPostType.event)
//             );
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 7: Posts with media content and specific user engagement
//      * Find posts with media that have been reacted to by specific users
//      */
//     public List<FeedPosts> findMediaPostsWithUserReactions(List<Long> userIds, Reactions.ReactionType reactionType) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Ensure it's a media post (photo type)
//             var mediaTypeCondition = cb.equal(root.get("type"), FeedPosts.FeedPostType.photo);
            
//             // Ensure it has media URLs
//             var hasMediaCondition = cb.isNotNull(root.get("mediaUrls"));
            
//             // Subquery to check if specific users have reacted
//             var reactionSubquery = query.subquery(Long.class);
//             var reactionRoot = reactionSubquery.from(Reactions.class);
//             reactionSubquery.select(cb.count(reactionRoot))
//                     .where(cb.and(
//                         cb.equal(reactionRoot.get("post"), root),
//                         cb.in(reactionRoot.get("user").get("id")).value(userIds),
//                         cb.equal(reactionRoot.get("reactionType"), reactionType)
//                     ));

//             var hasReactionsCondition = cb.greaterThan(reactionSubquery, 0L);

//             return cb.and(mediaTypeCondition, hasMediaCondition, hasReactionsCondition);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 8: Posts with birthday celebrations and user participation
//      * Find birthday posts where specific users are participants in the associated event
//      */
//     public List<FeedPosts> findBirthdayPostsWithParticipants(List<Long> participantIds) {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Ensure it's a birthday post
//             var birthdayCondition = cb.equal(root.get("type"), FeedPosts.FeedPostType.birthday);
            
//             // Join with Events to check participants
//             var eventJoin = root.join("events", jakarta.persistence.criteria.JoinType.INNER);
            
//             // Check if any of the specified users are participants
//             var participantCondition = cb.isMember(participantIds, eventJoin.get("participants"));

//             return cb.and(birthdayCondition, participantCondition);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 9: Posts with complex comment threading
//      * Find posts that have comments with replies (nested comments)
//      */
//     public List<FeedPosts> findPostsWithNestedComments() {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Subquery to find comments that have replies
//             var commentSubquery = query.subquery(Long.class);
//             var commentRoot = commentSubquery.from(Comments.class);
//             commentSubquery.select(cb.count(commentRoot))
//                     .where(cb.and(
//                         cb.equal(commentRoot.get("post"), root),
//                         cb.isNotNull(commentRoot.get("parentComment"))
//                     ));

//             return cb.greaterThan(commentSubquery, 0L);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 10: Posts with AI-generated content and user feedback
//      * Find AI-generated posts that have received positive reactions
//      */
//     public List<FeedPosts> findAiGeneratedPostsWithPositiveFeedback() {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Ensure it's AI-generated
//             var aiGeneratedCondition = cb.equal(root.get("isAiGenerated"), true);
            
//             // Subquery to find positive reactions (like, love, celebrate, support)
//             var positiveReactionSubquery = query.subquery(Long.class);
//             var reactionRoot = positiveReactionSubquery.from(Reactions.class);
//             positiveReactionSubquery.select(cb.count(reactionRoot))
//                     .where(cb.and(
//                         cb.equal(reactionRoot.get("post"), root),
//                         cb.in(reactionRoot.get("reactionType")).value(
//                             List.of(
//                                 Reactions.ReactionType.like,
//                                 Reactions.ReactionType.love,
//                                 Reactions.ReactionType.celebrate,
//                                 Reactions.ReactionType.support
//                             )
//                         )
//                     ));

//             var hasPositiveFeedbackCondition = cb.greaterThan(positiveReactionSubquery, 0L);

//             return cb.and(aiGeneratedCondition, hasPositiveFeedbackCondition);
//         };

//         return feedPostRepository.findAll(spec);
//     }

//     /**
//      * Example 11: Posts with pagination and complex sorting
//      * Find posts with engagement metrics and sort by engagement score
//      */
//     public Page<FeedPosts> findPostsWithEngagementScore(Pageable pageable) {
//         // This would require a custom repository method with @Query
//         // For demonstration, we'll return a simple specification
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Add any base conditions here
//             return cb.conjunction(); // Always true
//         };

//         return feedPostRepository.findAll(spec, pageable);
//     }

//     /**
//      * Example 12: Posts with cross-entity validation
//      * Find posts where the author has created events and received appreciations
//      */
//     public List<FeedPosts> findPostsByAuthorWithEventsAndAppreciations() {
//         Specification<FeedPosts> spec = (root, query, cb) -> {
//             // Subquery to check if author has created events
//             var eventSubquery = query.subquery(Long.class);
//             var eventRoot = eventSubquery.from(Events.class);
//             eventSubquery.select(cb.count(eventRoot))
//                     .where(cb.equal(eventRoot.get("parentPost").get("author"), root.get("author")));

//             // Subquery to check if author has received appreciations
//             var appreciationSubquery = query.subquery(Long.class);
//             var appreciationRoot = appreciationSubquery.from(Appreciations.class);
//             appreciationSubquery.select(cb.count(appreciationRoot))
//                     .where(cb.equal(appreciationRoot.get("toUser"), root.get("author")));

//             return cb.and(
//                 cb.greaterThan(eventSubquery, 0L),
//                 cb.greaterThan(appreciationSubquery, 0L)
//             );
//         };

//         return feedPostRepository.findAll(spec);
//     }
// } 