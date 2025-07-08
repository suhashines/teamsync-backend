package edu.teamsync.teamsync.services;

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
import edu.teamsync.teamsync.service.ReactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReactionServiceTest {

    @Mock
    private ReactionRepository reactionRepository;

    @Mock
    private FeedPostRepository feedPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReactionMapper reactionMapper;

    @InjectMocks
    private ReactionService reactionService;

    private final Long postId = 1L;
    private final Long userId = 1L;
    private final String reactionType = "like";

    private FeedPosts post;
    private Users user;
    private Reactions reaction;
    private ReactionCreateRequestDTO reactionCreateRequestDTO;
    private ReactionResponseDTO reactionResponseDTO;
    private List<Reactions> reactions;
    private List<ReactionResponseDTO> reactionResponseDTOList;

    @BeforeEach
    void setUp() {
        post = FeedPosts.builder()
                .id(postId)
                .build();

        user = Users.builder()
                .id(userId)
                .email("test@example.com")
                .build();

        reaction = Reactions.builder()
                .id(1L)
                .user(user)
                .post(post)
                .reactionType(Reactions.ReactionType.like)
                .createdAt(ZonedDateTime.now())
                .build();

        reactionCreateRequestDTO = new ReactionCreateRequestDTO();
        reactionCreateRequestDTO.setUserId(userId);
        reactionCreateRequestDTO.setReactionType(reactionType);

        reactionResponseDTO = ReactionResponseDTO.builder()
                .id(1L)
                .userId(userId)
                .reactionType(reactionType)
                .createdAt(ZonedDateTime.now())
                .build();

        reactions = Collections.singletonList(reaction);
        reactionResponseDTOList = Collections.singletonList(reactionResponseDTO);
    }

    @Test
    void getAllReactions_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(reactionRepository.findByPostId(postId)).thenReturn(reactions);
        when(reactionMapper.reactionsResponseToDTO(reactions)).thenReturn(reactionResponseDTOList);

        List<ReactionResponseDTO> result = reactionService.getAllReactions(postId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(reactionType, result.get(0).getReactionType());

        verify(feedPostRepository).findById(postId);
        verify(reactionRepository).findByPostId(postId);
        verify(reactionMapper).reactionsResponseToDTO(reactions);
    }

    @Test
    void getAllReactions_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reactionService.getAllReactions(postId));

        verify(feedPostRepository).findById(postId);
        verify(reactionRepository, never()).findByPostId(postId);
        verify(reactionMapper, never()).reactionsResponseToDTO(any());
    }

    @Test
    void addReaction_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Collections.emptyList());
        when(reactionRepository.save(any(Reactions.class))).thenReturn(reaction);

        assertDoesNotThrow(() -> reactionService.addReaction(postId, reactionCreateRequestDTO));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).findByUserIdAndPostId(userId, postId);
        verify(reactionRepository).save(any(Reactions.class));
    }

    @Test
    void addReaction_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reactionService.addReaction(postId, reactionCreateRequestDTO));

        verify(feedPostRepository).findById(postId);
        verify(userRepository, never()).findById(userId);
        verify(reactionRepository, never()).save(any(Reactions.class));
    }

    @Test
    void addReaction_UserNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reactionService.addReaction(postId, reactionCreateRequestDTO));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository, never()).save(any(Reactions.class));
    }

    @Test
    void addReaction_InvalidReactionType() {
        reactionCreateRequestDTO.setReactionType("invalid_type");
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> reactionService.addReaction(postId, reactionCreateRequestDTO));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository, never()).save(any(Reactions.class));
    }

    @Test
    void addReaction_ReactionAlreadyExists() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(userId, postId)).thenReturn(reactions);

        assertThrows(IllegalArgumentException.class, () -> reactionService.addReaction(postId, reactionCreateRequestDTO));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).findByUserIdAndPostId(userId, postId);
        verify(reactionRepository, never()).save(any(Reactions.class));
    }

    @Test
    void addReaction_DifferentReactionTypeAllowed() {
        // User has a different reaction type, so this should be allowed
        Reactions existingReaction = Reactions.builder()
                .id(2L)
                .user(user)
                .post(post)
                .reactionType(Reactions.ReactionType.love) // Different type
                .createdAt(ZonedDateTime.now())
                .build();

        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Collections.singletonList(existingReaction));
        when(reactionRepository.save(any(Reactions.class))).thenReturn(reaction);

        assertDoesNotThrow(() -> reactionService.addReaction(postId, reactionCreateRequestDTO));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).findByUserIdAndPostId(userId, postId);
        verify(reactionRepository).save(any(Reactions.class));
    }

    @Test
    void removeReaction_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(userId, postId)).thenReturn(reactions);

        assertDoesNotThrow(() -> reactionService.removeReaction(postId, userId, reactionType));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).findByUserIdAndPostId(userId, postId);
        verify(reactionRepository).delete(reaction);
    }

    @Test
    void removeReaction_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reactionService.removeReaction(postId, userId, reactionType));

        verify(feedPostRepository).findById(postId);
        verify(userRepository, never()).findById(userId);
        verify(reactionRepository, never()).delete(any(Reactions.class));
    }

    @Test
    void removeReaction_UserNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reactionService.removeReaction(postId, userId, reactionType));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository, never()).delete(any(Reactions.class));
    }

    @Test
    void removeReaction_InvalidReactionType() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> reactionService.removeReaction(postId, userId, "invalid_type"));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository, never()).delete(any(Reactions.class));
    }

    @Test
    void removeReaction_ReactionNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Collections.emptyList());

        assertThrows(NotFoundException.class, () -> reactionService.removeReaction(postId, userId, reactionType));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).findByUserIdAndPostId(userId, postId);
        verify(reactionRepository, never()).delete(any(Reactions.class));
    }

    @Test
    void removeReaction_SpecificReactionTypeNotFound() {
        // User has reactions but not the specific type being removed
        Reactions differentReaction = Reactions.builder()
                .id(2L)
                .user(user)
                .post(post)
                .reactionType(Reactions.ReactionType.love) // Different type
                .createdAt(ZonedDateTime.now())
                .build();

        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(reactionRepository.findByUserIdAndPostId(userId, postId)).thenReturn(Collections.singletonList(differentReaction));

        assertThrows(NotFoundException.class, () -> reactionService.removeReaction(postId, userId, reactionType));

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).findByUserIdAndPostId(userId, postId);
        verify(reactionRepository, never()).delete(any(Reactions.class));
    }
}
