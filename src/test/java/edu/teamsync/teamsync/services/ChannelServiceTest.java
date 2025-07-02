package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.channelDTO.ChannelRequestDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelResponseDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelUpdateDTO;
import edu.teamsync.teamsync.entity.Channels;
import edu.teamsync.teamsync.entity.Channels.ChannelType;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.ChannelMapper;
import edu.teamsync.teamsync.repository.ChannelRepository;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.ChannelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChannelServiceTest {

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ChannelMapper channelMapper;

    @InjectMocks
    private ChannelService channelService;

    private final Long channelId = 1L;
    private final Long projectId = 1L;
    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final String userEmail1 = "user1@example.com";
    private final String userEmail2 = "user2@example.com";

    private ChannelRequestDTO channelRequestDTO;
    private ChannelUpdateDTO channelUpdateDTO;
    private ChannelResponseDTO channelResponseDTO;
    private Channels channel;
    private Projects project;
    private Users user1, user2;

    @BeforeEach
    void setUp() {
        // Initialize test data
        project = Projects.builder()
                .id(projectId)
                .title("Test Project")
                .build();

        user1 = Users.builder()
                .id(userId1)
                .email(userEmail1)
                .build();

        user2 = Users.builder()
                .id(userId2)
                .email(userEmail2)
                .build();

        channelRequestDTO = new ChannelRequestDTO(
                "Test Channel",
                ChannelType.group,
                projectId,
                Arrays.asList(userId1, userId2)
        );

        channelUpdateDTO = new ChannelUpdateDTO(
                "Updated Channel",
                ChannelType.direct,
                projectId,
                Arrays.asList(userId1, userId2)
        );

        channel = Channels.builder()
                .id(channelId)
                .name("Test Channel")
                .type(ChannelType.group)
                .project(project)
                .members(Arrays.asList(userId1, userId2))
                .build();

        channelResponseDTO = new ChannelResponseDTO(
                channelId,
                "Test Channel",
                ChannelType.group,
                projectId,
                Arrays.asList(userId1, userId2)
        );
    }

    @Test
    void createChannel_Success() {
        // Given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(true);
        when(channelMapper.toEntity(channelRequestDTO)).thenReturn(channel);
        when(channelRepository.save(any(Channels.class))).thenReturn(channel);

        // When
        channelService.createChannel(channelRequestDTO);

        // Then
        verify(projectRepository).findById(projectId);
        verify(userRepository).existsById(userId1);
        verify(userRepository).existsById(userId2);
        verify(channelMapper).toEntity(channelRequestDTO);
        verify(channelRepository).save(any(Channels.class));
    }

    @Test
    void createChannel_ProjectNotFound_ThrowsNotFoundException() {
        // Given
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.createChannel(channelRequestDTO));

        assertEquals("Project with ID " + projectId + " not found", exception.getMessage());
        verify(projectRepository).findById(projectId);
        verifyNoInteractions(channelRepository);
    }

    @Test
    void createChannel_UserNotFound_ThrowsNotFoundException() {
        // Given
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.createChannel(channelRequestDTO));

        assertEquals("User with ID " + userId2 + " not found", exception.getMessage());
        verify(projectRepository).findById(projectId);
        verify(userRepository).existsById(userId1);
        verify(userRepository).existsById(userId2);
        verifyNoInteractions(channelRepository);
    }

    @Test
    void getChannelById_Success() {
        // Given
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(channelMapper.toDto(channel)).thenReturn(channelResponseDTO);

        // When
        ChannelResponseDTO result = channelService.getChannelById(channelId);

        // Then
        assertNotNull(result);
        assertEquals(channelResponseDTO.id(), result.id());
        assertEquals(channelResponseDTO.name(), result.name());
        assertEquals(channelResponseDTO.type(), result.type());
        assertEquals(channelResponseDTO.projectId(), result.projectId());
        assertEquals(channelResponseDTO.members(), result.members());

        verify(channelRepository).findById(channelId);
        verify(channelMapper).toDto(channel);
    }

    @Test
    void getChannelById_ChannelNotFound_ThrowsNotFoundException() {
        // Given
        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.getChannelById(channelId));

        assertEquals("Channel with ID " + channelId + " not found", exception.getMessage());
        verify(channelRepository).findById(channelId);
        verifyNoInteractions(channelMapper);
    }

    @Test
    void getAllChannels_Success() {
        // Given
        List<Channels> channels = Arrays.asList(channel);
        when(channelRepository.findAll()).thenReturn(channels);
        when(channelMapper.toDto(channel)).thenReturn(channelResponseDTO);

        // When
        List<ChannelResponseDTO> result = channelService.getAllChannels();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(channelResponseDTO.id(), result.get(0).id());
        assertEquals(channelResponseDTO.name(), result.get(0).name());

        verify(channelRepository).findAll();
        verify(channelMapper).toDto(channel);
    }

    @Test
    void getAllChannels_EmptyList() {
        // Given
        when(channelRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<ChannelResponseDTO> result = channelService.getAllChannels();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(channelRepository).findAll();
        verifyNoInteractions(channelMapper);
    }

    @Test
    void updateChannel_Success() {
        // Given
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));
        when(channelRepository.save(any(Channels.class))).thenReturn(channel);

        // When
        channelService.updateChannel(channelId, channelUpdateDTO);

        // Then
        verify(channelRepository).findById(channelId);
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId1);
        verify(userRepository).findById(userId2);
        verify(channelMapper).updateEntityFromDto(channelUpdateDTO, channel);
        verify(channelRepository).save(channel);
    }

    @Test
    void updateChannel_ChannelNotFound_ThrowsNotFoundException() {
        // Given
        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.updateChannel(channelId, channelUpdateDTO));

        assertEquals("Channel not found: " + channelId, exception.getMessage());
        verify(channelRepository).findById(channelId);
        verifyNoInteractions(projectRepository, userRepository, channelMapper);
    }

    @Test
    void updateChannel_ProjectNotFound_ThrowsNotFoundException() {
        // Given
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.updateChannel(channelId, channelUpdateDTO));

        assertEquals("Project not found: " + projectId, exception.getMessage());
        verify(channelRepository).findById(channelId);
        verify(projectRepository).findById(projectId);
        verifyNoInteractions(userRepository, channelMapper);
    }

    @Test
    void updateChannel_UserNotFound_ThrowsNotFoundException() {
        // Given
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(userRepository.findById(userId2)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.updateChannel(channelId, channelUpdateDTO));

        assertEquals("User with ID " + userId2 + " not found", exception.getMessage());
        verify(channelRepository).findById(channelId);
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId1);
        verify(userRepository).findById(userId2);
        verifyNoInteractions(channelMapper);
    }

    @Test
    void deleteChannel_Success() {
        // Given
        when(channelRepository.existsById(channelId)).thenReturn(true);

        // When
        channelService.deleteChannel(channelId);

        // Then
        verify(channelRepository).existsById(channelId);
        verify(channelRepository).deleteById(channelId);
    }

    @Test
    void deleteChannel_ChannelNotFound_ThrowsNotFoundException() {
        // Given
        when(channelRepository.existsById(channelId)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> channelService.deleteChannel(channelId));

        assertEquals("Channel with ID " + channelId + " not found", exception.getMessage());
        verify(channelRepository).existsById(channelId);
        verify(channelRepository, never()).deleteById(anyLong());
    }

    @Test
    void createChannel_WithSingleMember_Success() {
        // Given
        ChannelRequestDTO singleMemberRequest = new ChannelRequestDTO(
                "Direct Channel",
                ChannelType.direct,
                projectId,
                Arrays.asList(userId1)
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(channelMapper.toEntity(singleMemberRequest)).thenReturn(channel);
        when(channelRepository.save(any(Channels.class))).thenReturn(channel);

        // When
        channelService.createChannel(singleMemberRequest);

        // Then
        verify(projectRepository).findById(projectId);
        verify(userRepository).existsById(userId1);
        verify(channelMapper).toEntity(singleMemberRequest);
        verify(channelRepository).save(any(Channels.class));
    }

    @Test
    void updateChannel_WithDifferentMemberList_Success() {
        // Given
        ChannelUpdateDTO updateWithNewMembers = new ChannelUpdateDTO(
                "Updated Channel",
                ChannelType.group,
                projectId,
                Arrays.asList(userId1) // Only one member now
        );

        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
        when(channelRepository.save(any(Channels.class))).thenReturn(channel);

        // When
        channelService.updateChannel(channelId, updateWithNewMembers);

        // Then
        verify(channelRepository).findById(channelId);
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId1);
        verify(channelMapper).updateEntityFromDto(updateWithNewMembers, channel);
        verify(channelRepository).save(channel);
    }
}