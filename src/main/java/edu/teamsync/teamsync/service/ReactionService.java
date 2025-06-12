package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.Reactions;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.ReactionMapper;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.ReactionRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final FeedPostRepository feedPostRepository;
    private final UserRepository userRepository;
    private final ReactionMapper reactionMapper;

    public List<ReactionResponseDTO> getAllReactions(Long postId) {
        FeedPosts post = feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("FeedPost not found with id: " + postId));
        List<Reactions> reactions = reactionRepository.findByPostId(postId);
        return reactionMapper.reactionsResponseToDTO(reactions);
    }

    public void addReaction(Long postId, ReactionCreateRequestDTO request) {
        FeedPosts post = feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("FeedPost not found with id: " + postId));
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));

        Reactions.ReactionType reactionType;
        try {
            reactionType = Reactions.ReactionType.valueOf(request.getReactionType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid reaction type: " + request.getReactionType());
        }

        // Check if user already has this reaction on this post
        List<Reactions> existingReactions = reactionRepository.findByUserIdAndPostId(request.getUserId(), postId);
        boolean reactionExists = existingReactions.stream()
                .anyMatch(r -> r.getReactionType() == reactionType);

        if (reactionExists) {
            throw new IllegalArgumentException("User already has this reaction on this post");
        }

        // Create new reaction
        Reactions reaction = Reactions.builder()
                .user(user)
                .post(post)
                .reactionType(reactionType)
                .createdAt(ZonedDateTime.now())
                .build();

        reactionRepository.save(reaction);
//        return reactionMapper.reactionResponseToDTO(savedReaction);
    }

    public void removeReaction(Long postId, Long userId, String reactionType) {
        // Verify post exists
        FeedPosts post = feedPostRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("FeedPost not found with id: " + postId));

        // Verify user exists
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        // Validate reaction type
        try {
            Reactions.ReactionType.valueOf(reactionType);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid reaction type: " + reactionType);
        }

        // Find and remove the specific reaction
        List<Reactions> reactions = reactionRepository.findByUserIdAndPostId(userId, postId);

        Reactions targetReaction = reactions.stream()
                .filter(r -> r.getReactionType().name().equals(reactionType))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Reaction not found for user " + userId + " on post " + postId + " with type " + reactionType));

        reactionRepository.delete(targetReaction);
    }
}