package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.commentDTO.CommentCreateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentResponseDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentUpdateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.ReplyCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.entity.Comments;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.Reactions;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.CommentMapper;
import edu.teamsync.teamsync.mapper.ReactionMapper;
import edu.teamsync.teamsync.repository.CommentReactionRepository;
import edu.teamsync.teamsync.repository.CommentRepository;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final FeedPostRepository feedPostRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    private final ReactionMapper reactionMapper;
    private final CommentReactionRepository commentReactionRepository;

    public List<CommentResponseDTO> getAllCommentsByPostId(Long postId) {
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        List<Comments> comments = commentRepository.findByPostIdOrderByTimestamp(postId);
        return commentMapper.toResponseDTOList(comments);
    }

    public CommentResponseDTO getCommentById(Long postId, Long commentId) {
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        Comments comment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        return commentMapper.toResponseDTO(comment);
    }

    public void createComment(Long postId, CommentCreateRequestDTO requestDTO, String userEmail) {
        FeedPosts post = feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        Users author = userRepository.findByEmail(userEmail);
        if (author == null) {
            throw new NotFoundException("User not found ");
        }

        Comments parentComment = null;
        if (requestDTO.getParentCommentId() != null) {
            parentComment = commentRepository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found with id: " + requestDTO.getParentCommentId()));

            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentRepository.save(parentComment);
        }

        Comments comment = commentMapper.toEntity(requestDTO);
        comment.setPost(post);
        comment.setAuthor(author);
        comment.setParentComment(parentComment);
        comment.setTimestamp(ZonedDateTime.now());
        comment.setReplyCount(0);

        commentRepository.save(comment);
//        return commentMapper.toResponseDTO(savedComment);
    }

    public void updateComment(Long postId, Long commentId, CommentUpdateRequestDTO requestDTO) {
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        Comments existingComment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        commentMapper.updateEntityFromDTO(requestDTO, existingComment);

        if (requestDTO.getParentCommentId() != null &&
                !requestDTO.getParentCommentId().equals(existingComment.getParentComment() != null ? existingComment.getParentComment().getId() : null)) {

            Comments newParentComment = commentRepository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new NotFoundException("Parent comment not found with id: " + requestDTO.getParentCommentId()));
            existingComment.setParentComment(newParentComment);
        }

        if (requestDTO.getReactions() != null) {
            updateCommentReactions(existingComment, requestDTO.getReactions());
        }

       commentRepository.save(existingComment);
//        return mapCommentWithReactions(updatedComment);
    }

    public void deleteComment(Long postId, Long commentId) {
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        Comments comment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        if (comment.getParentComment() != null) {
            Comments parentComment = comment.getParentComment();
            parentComment.setReplyCount(Math.max(0, parentComment.getReplyCount() - 1));
            commentRepository.save(parentComment);
        }

        commentReactionRepository.deleteByCommentId(commentId);
        commentRepository.delete(comment);
    }


    public void updateCommentReactions(Comments comment, List<ReactionDetailDTO> reactionDTOs) {
        List<Reactions> existingReaction=commentReactionRepository.findByCommentId(comment.getId());
        if(!existingReaction.isEmpty())
        {
            commentReactionRepository.deleteByCommentId(comment.getId());
        }
        for (ReactionDetailDTO reactionDTO : reactionDTOs) {
            Users user = userRepository.findById(reactionDTO.getUserId())
                    .orElseThrow(() -> new NotFoundException("User not found with id: " + reactionDTO.getUserId()));

            Reactions reaction = Reactions.builder()
                    .comment(comment)
                    .user(user)
                    .reactionType(Reactions.ReactionType.valueOf(reactionDTO.getReactionType().toLowerCase()))
                    .createdAt(reactionDTO.getCreatedAt() != null ? reactionDTO.getCreatedAt() : ZonedDateTime.now())
                    .build();

            commentReactionRepository.save(reaction);
        }
    }

    public List<ReactionResponseDTO> getAllReactionsByCommentId(Long postId, Long commentId) {
        // Verify post exists
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        // Verify comment exists and belongs to the post
        commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        List<Reactions> reactions = commentReactionRepository.findByCommentId(commentId);
        return reactionMapper.reactionsResponseToDTO(reactions);
    }

    public void addReactionToComment(Long postId, Long commentId, ReactionCreateRequestDTO requestDTO) {
        // Verify post exists
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        // Verify comment exists and belongs to the post
        Comments comment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        Users user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + requestDTO.getUserId()));

        // Validate reaction type by attempting to parse it
        Reactions.ReactionType reactionType;
        try {
            reactionType = Reactions.ReactionType.valueOf(requestDTO.getReactionType().toLowerCase());
        } catch (IllegalArgumentException e) {
            // This will be caught by ValidationExceptionHandler as IllegalArgumentException
            throw new IllegalArgumentException("Invalid reaction type: " + requestDTO.getReactionType());
        }

        // Check if user already has this specific reaction on this comment
        Optional<Reactions> existingReaction = commentReactionRepository.findByUserIdAndCommentIdAndReactionType(
                user.getId(), commentId, reactionType);

        if (existingReaction.isPresent()) {
            // This will be caught by ValidationExceptionHandler as IllegalArgumentException
            throw new IllegalArgumentException("User already has this reaction on this comment");
        }

        // Create new reaction
        Reactions reaction = Reactions.builder()
                .user(user)
                .comment(comment)
                .reactionType(reactionType)
                .createdAt(ZonedDateTime.now())
                .build();

          commentReactionRepository.save(reaction);
//        return reactionMapper.reactionResponseToDTO(savedReaction);
    }

    public void removeReactionFromComment(Long postId, Long commentId, Long userId, String reactionTypeStr) {
        // Verify post exists
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        // Verify comment exists and belongs to the post
        commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Validate reaction type
        Reactions.ReactionType reactionType;
        try {
            reactionType = Reactions.ReactionType.valueOf(reactionTypeStr.toLowerCase());
        } catch (IllegalArgumentException e) {
            // This will be caught by ValidationExceptionHandler as IllegalArgumentException
            throw new IllegalArgumentException("Invalid reaction type: " + reactionTypeStr);
        }

        // Check if the specific reaction exists
        Optional<Reactions> existingReaction = commentReactionRepository.findByUserIdAndCommentIdAndReactionType(
                userId, commentId, reactionType);

        if (existingReaction.isEmpty()) {
            throw new NotFoundException("Reaction not found for user " + userId + " on comment " + commentId + " with type " + reactionTypeStr);
        }

        // Delete the specific reaction - any database constraint violations will be handled by DBExceptionHandler
        commentReactionRepository.deleteByUserIdAndCommentIdAndReactionType(userId, commentId, reactionType);
    }

    public List<CommentResponseDTO> getAllRepliesByCommentId(Long postId, Long commentId) {
        // Verify post exists
        feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        // Verify parent comment exists and belongs to the post
        commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        List<Comments> replies = commentRepository.findRepliesByPostIdAndParentCommentId(postId, commentId);
        return commentMapper.toResponseDTOList(replies);
    }

    public void addReplyToComment(Long postId, Long commentId, ReplyCreateRequestDTO requestDTO) {
        // Verify post exists
        FeedPosts post = feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found with id: " + postId));

        // Verify parent comment exists and belongs to the post
        Comments parentComment = commentRepository.findByPostIdAndCommentId(postId, commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found with id: " + commentId + " for post: " + postId));

        // Verify user exists
        Users author = userRepository.findById(requestDTO.getAuthor_id())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + requestDTO.getAuthor_id()));

        // Create reply comment
        Comments reply = Comments.builder()
                .post(post)
                .author(author)
                .content(requestDTO.getContent())
                .parentComment(parentComment)
                .timestamp(ZonedDateTime.now())
                .replyCount(0)
                .build();

        // Increment parent comment's reply count
        parentComment.setReplyCount(parentComment.getReplyCount() + 1);

        // Save both entities
        commentRepository.save(parentComment);
        commentRepository.save(reply);
    }
}
