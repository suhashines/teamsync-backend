package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteCreationDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteResponseDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteUpdateDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.PollVotes;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.PollVoteMapper;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.PollVoteRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.PollVoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PollVoteServiceTest {

    @Mock
    private PollVoteRepository pollVoteRepository;

    @Mock
    private FeedPostRepository feedPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PollVoteMapper pollVoteMapper;

    @InjectMocks
    private PollVoteService pollVoteService;

    private final Long pollVoteId = 1L;
    private final Long pollId = 1L;
    private final Long userId = 1L;
    private final String userEmail = "test@example.com";
    private final String selectedOption = "Option A";
    private final String[] pollOptions = {"Option A", "Option B", "Option C"};

    private FeedPosts poll;
    private FeedPosts nonPoll;
    private Users user;
    private PollVotes pollVote;
    private PollVoteCreationDTO creationDTO;
    private PollVoteUpdateDTO updateDTO;
    private PollVoteResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        poll = FeedPosts.builder()
                .id(pollId)
                .type(FeedPosts.FeedPostType.poll)
                .pollOptions(pollOptions)
                .build();

        nonPoll = FeedPosts.builder()
                .id(2L)
                .type(FeedPosts.FeedPostType.text)
                .build();

        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .build();

        pollVote = PollVotes.builder()
                .id(pollVoteId)
                .poll(poll)
                .user(user)
                .selectedOption(selectedOption)
                .build();

        creationDTO = new PollVoteCreationDTO();
        creationDTO.setPollId(pollId);
        creationDTO.setSelectedOption(selectedOption);

        updateDTO = new PollVoteUpdateDTO();
        updateDTO.setPollId(pollId);
        updateDTO.setUserId(userId);
        updateDTO.setSelectedOption("Option B");

        responseDTO = PollVoteResponseDTO.builder()
                .id(pollVoteId)
                .pollId(pollId)
                .userId(userId)
                .selectedOption(selectedOption)
                .build();
    }

    @Test
    void getAllPollVotes_Success() {
        when(pollVoteRepository.findAll()).thenReturn(Collections.singletonList(pollVote));
        when(pollVoteMapper.toDTO(pollVote)).thenReturn(responseDTO);

        List<PollVoteResponseDTO> result = pollVoteService.getAllPollVotes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pollVoteId, result.get(0).getId());
        assertEquals(pollId, result.get(0).getPollId());
        assertEquals(userId, result.get(0).getUserId());
        assertEquals(selectedOption, result.get(0).getSelectedOption());
        verify(pollVoteRepository).findAll();
        verify(pollVoteMapper).toDTO(pollVote);
    }

    @Test
    void getAllPollVotes_EmptyList() {
        when(pollVoteRepository.findAll()).thenReturn(Collections.emptyList());

        List<PollVoteResponseDTO> result = pollVoteService.getAllPollVotes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(pollVoteRepository).findAll();
        verify(pollVoteMapper, never()).toDTO(any());
    }

    @Test
    void getPollVoteById_Success() {
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.of(pollVote));
        when(pollVoteMapper.toDTO(pollVote)).thenReturn(responseDTO);

        PollVoteResponseDTO result = pollVoteService.getPollVoteById(pollVoteId);

        assertNotNull(result);
        assertEquals(pollVoteId, result.getId());
        assertEquals(pollId, result.getPollId());
        assertEquals(userId, result.getUserId());
        assertEquals(selectedOption, result.getSelectedOption());
        verify(pollVoteRepository).findById(pollVoteId);
        verify(pollVoteMapper).toDTO(pollVote);
    }

    @Test
    void getPollVoteById_NotFound() {
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pollVoteService.getPollVoteById(pollVoteId));
        verify(pollVoteRepository).findById(pollVoteId);
        verify(pollVoteMapper, never()).toDTO(any());
    }

    @Test
    void createPollVote_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.save(any(PollVotes.class))).thenReturn(pollVote);

        pollVoteService.createPollVote(creationDTO, userEmail);

        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository).findById(pollId);
        verify(pollVoteRepository).save(any(PollVotes.class));
    }

    @Test
    void createPollVote_UserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository, never()).findById(any());
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void createPollVote_PollNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository).findById(pollId);
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void createPollVote_PostIsNotPoll() {
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(nonPoll));

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository).findById(pollId);
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void createPollVote_InvalidPollOption() {
        creationDTO.setSelectedOption("Invalid Option");
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository).findById(pollId);
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void createPollVote_NullPollOptions() {
        FeedPosts pollWithNullOptions = FeedPosts.builder()
                .id(pollId)
                .type(FeedPosts.FeedPostType.poll)
                .pollOptions(null)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(pollWithNullOptions));

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository).findById(pollId);
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void createPollVote_CaseInsensitiveOption() {
        creationDTO.setSelectedOption("option a"); // lowercase
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.save(any(PollVotes.class))).thenReturn(pollVote);

        pollVoteService.createPollVote(creationDTO, userEmail);

        verify(userRepository).findByEmail(userEmail);
        verify(feedPostRepository).findById(pollId);
        verify(pollVoteRepository).save(any(PollVotes.class));
    }

    @Test
    void updatePollVote_Success() {
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.of(pollVote));
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(pollVoteRepository.save(any(PollVotes.class))).thenReturn(pollVote);

        pollVoteService.updatePollVote(pollVoteId, updateDTO);

        verify(pollVoteRepository).findById(pollVoteId);
        verify(feedPostRepository).findById(pollId);
        verify(userRepository).findById(userId);
        verify(pollVoteRepository).save(any(PollVotes.class));
    }

    @Test
    void updatePollVote_PollVoteNotFound() {
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pollVoteService.updatePollVote(pollVoteId, updateDTO));
        verify(pollVoteRepository).findById(pollVoteId);
        verify(feedPostRepository, never()).findById(any());
        verify(userRepository, never()).findById(any());
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void updatePollVote_PollNotFound() {
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.of(pollVote));
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pollVoteService.updatePollVote(pollVoteId, updateDTO));
        verify(pollVoteRepository).findById(pollVoteId);
        verify(feedPostRepository).findById(pollId);
        verify(userRepository, never()).findById(any());
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void updatePollVote_UserNotFound() {
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.of(pollVote));
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> pollVoteService.updatePollVote(pollVoteId, updateDTO));
        verify(pollVoteRepository).findById(pollVoteId);
        verify(feedPostRepository).findById(pollId);
        verify(userRepository).findById(userId);
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void updatePollVote_InvalidPollOption() {
        updateDTO.setSelectedOption("Invalid Option");
        when(pollVoteRepository.findById(pollVoteId)).thenReturn(Optional.of(pollVote));
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> pollVoteService.updatePollVote(pollVoteId, updateDTO));
        verify(pollVoteRepository).findById(pollVoteId);
        verify(feedPostRepository).findById(pollId);
        verify(userRepository).findById(userId);
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void deletePollVote_Success() {
        when(pollVoteRepository.existsById(pollVoteId)).thenReturn(true);

        pollVoteService.deletePollVote(pollVoteId);

        verify(pollVoteRepository).existsById(pollVoteId);
        verify(pollVoteRepository).deleteById(pollVoteId);
    }

    @Test
    void deletePollVote_NotFound() {
        when(pollVoteRepository.existsById(pollVoteId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> pollVoteService.deletePollVote(pollVoteId));
        verify(pollVoteRepository).existsById(pollVoteId);
        verify(pollVoteRepository, never()).deleteById(any());
    }

    @Test
    void isValidPollOption_ValidOption() {
        // This tests the private method indirectly through createPollVote
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.save(any(PollVotes.class))).thenReturn(pollVote);

        assertDoesNotThrow(() -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(pollVoteRepository).save(any(PollVotes.class));
    }

    @Test
    void isValidPollOption_WithWhitespace() {
        creationDTO.setSelectedOption("  Option A  "); // with whitespace
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));
        when(pollVoteRepository.save(any(PollVotes.class))).thenReturn(pollVote);

        assertDoesNotThrow(() -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(pollVoteRepository).save(any(PollVotes.class));
    }

    @Test
    void isValidPollOption_EmptyPollOptions() {
        FeedPosts pollWithEmptyOptions = FeedPosts.builder()
                .id(pollId)
                .type(FeedPosts.FeedPostType.poll)
                .pollOptions(new String[0])
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(pollWithEmptyOptions));

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void isValidPollOption_NullSelectedOption() {
        creationDTO.setSelectedOption(null);
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(poll));

        assertThrows(NotFoundException.class, () -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(pollVoteRepository, never()).save(any());
    }

    @Test
    void isValidPollOption_PollOptionsWithNullElements() {
        String[] pollOptionsWithNull = {"Option A", null, "Option B"};
        FeedPosts pollWithNullElements = FeedPosts.builder()
                .id(pollId)
                .type(FeedPosts.FeedPostType.poll)
                .pollOptions(pollOptionsWithNull)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(feedPostRepository.findById(pollId)).thenReturn(Optional.of(pollWithNullElements));
        when(pollVoteRepository.save(any(PollVotes.class))).thenReturn(pollVote);

        assertDoesNotThrow(() -> pollVoteService.createPollVote(creationDTO, userEmail));
        verify(pollVoteRepository).save(any(PollVotes.class));
    }
}
