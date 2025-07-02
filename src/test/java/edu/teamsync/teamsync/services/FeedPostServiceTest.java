package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.feedPostDTO.*;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.Reactions;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.FeedPostMapper;
import edu.teamsync.teamsync.mapper.ReactionMapper;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.ReactionRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.FeedPostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedPostServiceTest {

    @Mock
    private FeedPostRepository feedPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FeedPostMapper feedPostMapper;

    @Mock
    private ReactionMapper reactionMapper;

    @Mock
    private ReactionRepository reactionRepository;

    @InjectMocks
    private FeedPostService feedPostService;

    private final Long feedPostId = 1L;
    private final Long userId = 1L;
    private final String userEmail = "test@example.com";

    private FeedPosts feedPost;
    private Users user;
    private FeedPostCreateRequest createRequest;
    private FeedPostUpdateRequest updateRequest;
    private FeedPostResponseDTO responseDTO;
    private FeedPostWithReactionDTO withReactionDTO;
    private List<ReactionDetailDTO> reactionDetailDTOList;
    private List<Reactions> reactionsList;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .build();

        feedPost = FeedPosts.builder()
                .id(feedPostId)
                .type(FeedPosts.FeedPostType.text)
                .author(user)
                .content("Test content")
                .createdAt(ZonedDateTime.now())
                .eventDate(LocalDate.now())
                .mediaUrls(new String[]{"url1", "url2"})
                .pollOptions(new String[]{"option1", "option2"})
                .isAiGenerated(false)
                .aiSummary("Test summary")
                .build();

        createRequest = new FeedPostCreateRequest();
        createRequest.setType(FeedPosts.FeedPostType.text);
        createRequest.setContent("New post content");
        createRequest.setEventDate(LocalDate.now());
        createRequest.setMediaUrls(new String[]{"url1", "url2"});
        createRequest.setPollOptions(new String[]{"option1", "option2"});

        updateRequest = new FeedPostUpdateRequest();
        updateRequest.setType(FeedPosts.FeedPostType.photo);
        updateRequest.setAuthorId(userId);
        updateRequest.setContent("Updated content");
        updateRequest.setEventDate(LocalDate.now());
        updateRequest.setMediaUrls(new String[]{"updated_url1"});
        updateRequest.setPollOptions(new String[]{"updated_option1"});
        updateRequest.setIsAiGenerated(true);
        updateRequest.setAiSummary("Updated summary");

        ReactionDetailDTO reactionDetailDTO = new ReactionDetailDTO();
        reactionDetailDTO.setUserId(userId);
        reactionDetailDTO.setReactionType("like");
        reactionDetailDTO.setCreatedAt(ZonedDateTime.now());
        reactionDetailDTOList = Collections.singletonList(reactionDetailDTO);
        updateRequest.setReactions(reactionDetailDTOList);

        responseDTO = new FeedPostResponseDTO();
        responseDTO.setId(feedPostId);
        responseDTO.setType(FeedPosts.FeedPostType.text);
        responseDTO.setAuthorId(userId);
        responseDTO.setContent("Test content");
        responseDTO.setCreatedAt(feedPost.getCreatedAt());
        responseDTO.setEventDate(feedPost.getEventDate());
        responseDTO.setMediaUrls(feedPost.getMediaUrls());
        responseDTO.setPollOptions(feedPost.getPollOptions());
        responseDTO.setAiGenerated(false);
        responseDTO.setAiSummary("Test summary");

        withReactionDTO = new FeedPostWithReactionDTO();
        withReactionDTO.setId(feedPostId);
        withReactionDTO.setType(FeedPosts.FeedPostType.text);
        withReactionDTO.setAuthorId(userId);
        withReactionDTO.setContent("Test content");
        withReactionDTO.setCreatedAt(feedPost.getCreatedAt());
        withReactionDTO.setEventDate(feedPost.getEventDate());
        withReactionDTO.setMediaUrls(feedPost.getMediaUrls());
        withReactionDTO.setPollOptions(feedPost.getPollOptions());
        withReactionDTO.setAiGenerated(false);
        withReactionDTO.setAiSummary("Test summary");
        withReactionDTO.setReactions(reactionDetailDTOList);

        Reactions reaction = Reactions.builder()
                .id(1L)
                .user(user)
                .post(feedPost)
                .reactionType(Reactions.ReactionType.like)
                .createdAt(ZonedDateTime.now())
                .build();
        reactionsList = Collections.singletonList(reaction);
    }

    @Test
    void getAllFeedPosts_Success() {
        when(feedPostRepository.findAll()).thenReturn(Collections.singletonList(feedPost));
        when(feedPostMapper.toResponseList(anyList())).thenReturn(Collections.singletonList(responseDTO));

        List<FeedPostResponseDTO> result = feedPostService.getAllFeedPosts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(feedPostId, result.get(0).getId());
        assertEquals("Test content", result.get(0).getContent());
        verify(feedPostRepository).findAll();
        verify(feedPostMapper).toResponseList(anyList());
    }

    @Test
    void getAllFeedPosts_EmptyList() {
        when(feedPostRepository.findAll()).thenReturn(Collections.emptyList());
        when(feedPostMapper.toResponseList(anyList())).thenReturn(Collections.emptyList());

        List<FeedPostResponseDTO> result = feedPostService.getAllFeedPosts();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(feedPostRepository).findAll();
        verify(feedPostMapper).toResponseList(anyList());
    }

    @Test
    void createFeedPost_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostMapper.toEntity(createRequest)).thenReturn(feedPost);
        when(feedPostRepository.save(any(FeedPosts.class))).thenReturn(feedPost);

        feedPostService.createFeedPost(createRequest, userEmail);

        verify(userRepository).findByEmail(userEmail);
        verify(feedPostMapper).toEntity(createRequest);
        verify(feedPostRepository).save(any(FeedPosts.class));
    }

    @Test
    void createFeedPost_UserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> feedPostService.createFeedPost(createRequest, userEmail));

        assertEquals("User not found with email " + userEmail, exception.getMessage());
        verify(userRepository).findByEmail(userEmail);
        verify(feedPostMapper, never()).toEntity(any());
        verify(feedPostRepository, never()).save(any());
    }

    @Test
    void getFeedPostById_Success() {
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.of(feedPost));
        when(reactionRepository.findByPostId(feedPostId)).thenReturn(reactionsList);
        when(reactionMapper.reactionsToDTO(reactionsList)).thenReturn(reactionDetailDTOList);
        when(feedPostMapper.toDetailDtoWithReactions(feedPost, reactionDetailDTOList)).thenReturn(withReactionDTO);

        FeedPostWithReactionDTO result = feedPostService.getFeedPostById(feedPostId);

        assertNotNull(result);
        assertEquals(feedPostId, result.getId());
        assertEquals("Test content", result.getContent());
        assertEquals(1, result.getReactions().size());
        verify(feedPostRepository).findById(feedPostId);
        verify(reactionRepository).findByPostId(feedPostId);
        verify(reactionMapper).reactionsToDTO(reactionsList);
        verify(feedPostMapper).toDetailDtoWithReactions(feedPost, reactionDetailDTOList);
    }

    @Test
    void getFeedPostById_NotFound() {
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> feedPostService.getFeedPostById(feedPostId));

        assertEquals("FeedPost not found with id: " + feedPostId, exception.getMessage());
        verify(feedPostRepository).findById(feedPostId);
        verify(reactionRepository, never()).findByPostId(any());
        verify(reactionMapper, never()).reactionsToDTO(any());
        verify(feedPostMapper, never()).toDetailDtoWithReactions(any(), any());
    }

    @Test
    void updateFeedPost_Success() {
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.of(feedPost));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        when(feedPostRepository.save(any(FeedPosts.class))).thenReturn(feedPost);
        when(reactionRepository.save(any(Reactions.class))).thenReturn(new Reactions());

        feedPostService.updateFeedPost(feedPostId, updateRequest);

        verify(feedPostRepository).findById(feedPostId);
        verify(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        verify(reactionRepository).deleteByPostId(feedPostId);
        verify(userRepository).findById(userId);
        verify(reactionRepository).save(any(Reactions.class));
        verify(feedPostRepository).save(feedPost);
    }

    @Test
    void updateFeedPost_NotFound() {
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> feedPostService.updateFeedPost(feedPostId, updateRequest));

        assertEquals("FeedPost not found with id: " + feedPostId, exception.getMessage());
        verify(feedPostRepository).findById(feedPostId);
        verify(feedPostMapper, never()).updateEntityFromRequest(any(), any());
        verify(feedPostRepository, never()).save(any());
    }

    @Test
    void updateFeedPost_WithoutReactions() {
        updateRequest.setReactions(null);
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.of(feedPost));
        doNothing().when(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        when(feedPostRepository.save(any(FeedPosts.class))).thenReturn(feedPost);

        feedPostService.updateFeedPost(feedPostId, updateRequest);

        verify(feedPostRepository).findById(feedPostId);
        verify(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        verify(reactionRepository, never()).deleteByPostId(any());
        verify(userRepository, never()).findById(any());
        verify(reactionRepository, never()).save(any(Reactions.class));
        verify(feedPostRepository).save(feedPost);
    }

    @Test
    void updateFeedPost_UserNotFoundForReaction() {
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.of(feedPost));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        doNothing().when(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> feedPostService.updateFeedPost(feedPostId, updateRequest));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(feedPostRepository).findById(feedPostId);
        verify(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        verify(reactionRepository).deleteByPostId(feedPostId);
        verify(userRepository).findById(userId);
        verify(reactionRepository, never()).save(any(Reactions.class));
        verify(feedPostRepository, never()).save(any());
    }

    @Test
    void deleteFeedPost_Success() {
        when(feedPostRepository.existsById(feedPostId)).thenReturn(true);
        doNothing().when(reactionRepository).deleteByPostId(feedPostId);
        doNothing().when(feedPostRepository).deleteById(feedPostId);

        feedPostService.deleteFeedPost(feedPostId);

        verify(feedPostRepository).existsById(feedPostId);
        verify(reactionRepository).deleteByPostId(feedPostId);
        verify(feedPostRepository).deleteById(feedPostId);
    }

    @Test
    void deleteFeedPost_NotFound() {
        when(feedPostRepository.existsById(feedPostId)).thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> feedPostService.deleteFeedPost(feedPostId));

        assertEquals("FeedPost not found with id: " + feedPostId, exception.getMessage());
        verify(feedPostRepository).existsById(feedPostId);
        verify(reactionRepository, never()).deleteByPostId(any());
        verify(feedPostRepository, never()).deleteById(any());
    }

    @Test
    void updateFeedPost_EmptyReactionsList() {
        updateRequest.setReactions(Collections.emptyList());
        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.of(feedPost));
        doNothing().when(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        when(feedPostRepository.save(any(FeedPosts.class))).thenReturn(feedPost);

        feedPostService.updateFeedPost(feedPostId, updateRequest);

        verify(feedPostRepository).findById(feedPostId);
        verify(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        verify(reactionRepository).deleteByPostId(feedPostId);
        verify(userRepository, never()).findById(any());
        verify(reactionRepository, never()).save(any(Reactions.class));
        verify(feedPostRepository).save(feedPost);
    }

    @Test
    void updateFeedPost_MultipleReactions() {
        ReactionDetailDTO reaction1 = new ReactionDetailDTO();
        reaction1.setUserId(userId);
        reaction1.setReactionType("like");
        reaction1.setCreatedAt(ZonedDateTime.now());

        ReactionDetailDTO reaction2 = new ReactionDetailDTO();
        reaction2.setUserId(2L);
        reaction2.setReactionType("love");
        reaction2.setCreatedAt(ZonedDateTime.now());

        updateRequest.setReactions(List.of(reaction1, reaction2));

        Users user2 = Users.builder().id(2L).email("user2@example.com").build();

        when(feedPostRepository.findById(feedPostId)).thenReturn(Optional.of(feedPost));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        doNothing().when(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        when(feedPostRepository.save(any(FeedPosts.class))).thenReturn(feedPost);
        when(reactionRepository.save(any(Reactions.class))).thenReturn(new Reactions());

        feedPostService.updateFeedPost(feedPostId, updateRequest);

        verify(feedPostRepository).findById(feedPostId);
        verify(feedPostMapper).updateEntityFromRequest(updateRequest, feedPost);
        verify(reactionRepository).deleteByPostId(feedPostId);
        verify(userRepository).findById(userId);
        verify(userRepository).findById(2L);
        verify(reactionRepository, times(2)).save(any(Reactions.class));
        verify(feedPostRepository).save(feedPost);
    }
}
