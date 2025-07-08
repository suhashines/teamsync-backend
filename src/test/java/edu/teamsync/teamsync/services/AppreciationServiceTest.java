package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationCreateDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationResponseDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationUpdateDTO;
import edu.teamsync.teamsync.entity.Appreciations;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.exception.http.UnauthorizedException;
import edu.teamsync.teamsync.mapper.AppreciationMapper;
import edu.teamsync.teamsync.repository.AppreciationRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.AppreciationService;
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
public class AppreciationServiceTest {

    @Mock
    private AppreciationRepository appreciationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppreciationMapper appreciationMapper;

    @InjectMocks
    private AppreciationService appreciationService;

    private final Long appreciationId = 1L;
    private final Long fromUserId = 1L;
    private final Long toUserId = 2L;
    private final String userEmail = "test@example.com";
    private final String fromUserEmail = "from@example.com";

    private Users fromUser;
    private Users toUser;
    private Users authenticatedUser;
    private Appreciations appreciation;
    private AppreciationCreateDTO createDTO;
    private AppreciationUpdateDTO updateDTO;
    private AppreciationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        fromUser = Users.builder()
                .id(fromUserId)
                .email(fromUserEmail)
                .build();

        toUser = Users.builder()
                .id(toUserId)
                .email("to@example.com")
                .build();

        authenticatedUser = Users.builder()
                .id(fromUserId)
                .email(userEmail)
                .build();

        appreciation = Appreciations.builder()
                .id(appreciationId)
                .fromUser(fromUser)
                .toUser(toUser)
                .message("Great job!")
                .timestamp(ZonedDateTime.now())
                .build();

        createDTO = new AppreciationCreateDTO();
        createDTO.setToUserId(toUserId);
        createDTO.setMessage("Great work!");

        updateDTO = new AppreciationUpdateDTO();
        updateDTO.setFromUserId(fromUserId);
        updateDTO.setToUserId(toUserId);
        updateDTO.setMessage("Updated message");

        responseDTO = new AppreciationResponseDTO();
        responseDTO.setId(appreciationId);
        responseDTO.setFromUserId(fromUserId);
        responseDTO.setToUserId(toUserId);
        responseDTO.setMessage("Great job!");
        responseDTO.setTimestamp(appreciation.getTimestamp());
    }

    @Test
    void getAllAppreciations_Success() {
        when(appreciationRepository.findAll()).thenReturn(Collections.singletonList(appreciation));
        when(appreciationMapper.toResponseDTO(appreciation)).thenReturn(responseDTO);

        List<AppreciationResponseDTO> result = appreciationService.getAllAppreciations();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appreciationId, result.get(0).getId());
        verify(appreciationRepository).findAll();
        verify(appreciationMapper).toResponseDTO(appreciation);
    }

    @Test
    void getAllAppreciations_EmptyList() {
        when(appreciationRepository.findAll()).thenReturn(Collections.emptyList());

        List<AppreciationResponseDTO> result = appreciationService.getAllAppreciations();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appreciationRepository).findAll();
        verify(appreciationMapper, never()).toResponseDTO(any());
    }

    @Test
    void getAppreciationById_Success() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(appreciationMapper.toResponseDTO(appreciation)).thenReturn(responseDTO);

        AppreciationResponseDTO result = appreciationService.getAppreciationById(appreciationId);

        assertNotNull(result);
        assertEquals(appreciationId, result.getId());
        verify(appreciationRepository).findById(appreciationId);
        verify(appreciationMapper).toResponseDTO(appreciation);
    }

    @Test
    void getAppreciationById_NullId() {
        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.getAppreciationById(null));
        verify(appreciationRepository, never()).findById(any());
    }

    @Test
    void getAppreciationById_NotFound() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appreciationService.getAppreciationById(appreciationId));
        verify(appreciationRepository).findById(appreciationId);
        verify(appreciationMapper, never()).toResponseDTO(any());
    }

    @Test
    void createAppreciation_Success() {
        when(userRepository.findByEmail(userEmail)).thenReturn(fromUser);
        when(userRepository.findById(toUserId)).thenReturn(Optional.of(toUser));
        when(appreciationMapper.toEntity(createDTO)).thenReturn(appreciation);
        when(appreciationRepository.save(any(Appreciations.class))).thenReturn(appreciation);

        appreciationService.createAppreciation(createDTO, userEmail);

        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(toUserId);
        verify(appreciationMapper).toEntity(createDTO);
        verify(appreciationRepository).save(any(Appreciations.class));
    }

    @Test
    void createAppreciation_NullCreateDTO() {
        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.createAppreciation(null, userEmail));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void createAppreciation_NullUserEmail() {
        assertThrows(UnauthorizedException.class,
                () -> appreciationService.createAppreciation(createDTO, null));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void createAppreciation_EmptyUserEmail() {
        assertThrows(UnauthorizedException.class,
                () -> appreciationService.createAppreciation(createDTO, "   "));
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void createAppreciation_FromUserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UnauthorizedException.class,
                () -> appreciationService.createAppreciation(createDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void createAppreciation_NullToUserId() {
        createDTO.setToUserId(null);
        when(userRepository.findByEmail(userEmail)).thenReturn(fromUser);

        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.createAppreciation(createDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void createAppreciation_ToUserNotFound() {
        when(userRepository.findByEmail(userEmail)).thenReturn(fromUser);
        when(userRepository.findById(toUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appreciationService.createAppreciation(createDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(toUserId);
    }

    @Test
    void createAppreciation_SelfAppreciation() {
        createDTO.setToUserId(fromUserId);
        when(userRepository.findByEmail(userEmail)).thenReturn(fromUser);
        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));

        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.createAppreciation(createDTO, userEmail));
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(fromUserId);
    }

    @Test
    void updateAppreciation_Success() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(authenticatedUser);
        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(toUserId)).thenReturn(Optional.of(toUser));
        doNothing().when(appreciationMapper).updateEntityFromDTO(updateDTO, appreciation);
        when(appreciationRepository.save(appreciation)).thenReturn(appreciation);

        appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail);

        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(fromUserId);
        verify(userRepository).findById(toUserId);
        verify(appreciationMapper).updateEntityFromDTO(updateDTO, appreciation);
        verify(appreciationRepository).save(appreciation);
    }

    @Test
    void updateAppreciation_NullId() {
        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.updateAppreciation(null, updateDTO, userEmail));
        verify(appreciationRepository, never()).findById(any());
    }

    @Test
    void updateAppreciation_NullUpdateDTO() {
        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.updateAppreciation(appreciationId, null, userEmail));
        verify(appreciationRepository, never()).findById(any());
    }

    @Test
    void updateAppreciation_NullUserEmail() {
        assertThrows(UnauthorizedException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, null));
        verify(appreciationRepository, never()).findById(any());
    }

    @Test
    void updateAppreciation_EmptyUserEmail() {
        assertThrows(UnauthorizedException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, "   "));
        verify(appreciationRepository, never()).findById(any());
    }

    @Test
    void updateAppreciation_AppreciationNotFound() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail));
        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void updateAppreciation_AuthenticatedUserNotFound() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(UnauthorizedException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail));
        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void updateAppreciation_FromUserNotFound() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(authenticatedUser);
        when(userRepository.findById(fromUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail));
        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(fromUserId);
    }

    @Test
    void updateAppreciation_ToUserNotFound() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(authenticatedUser);
        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));
        when(userRepository.findById(toUserId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail));
        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(fromUserId);
        verify(userRepository).findById(toUserId);
    }

    @Test
    void updateAppreciation_SelfAppreciationUpdate() {
        // Setup appreciation with same from and to user
        updateDTO.setToUserId(fromUserId);
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(authenticatedUser);
        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));

        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail));
        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository, times(2)).findById(fromUserId); // Called twice: once for fromUserId, once for toUserId
    }

    @Test
    void updateAppreciation_WithoutFromUserIdUpdate() {
        updateDTO.setFromUserId(null);
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(authenticatedUser);
        when(userRepository.findById(toUserId)).thenReturn(Optional.of(toUser));
        doNothing().when(appreciationMapper).updateEntityFromDTO(updateDTO, appreciation);
        when(appreciationRepository.save(appreciation)).thenReturn(appreciation);

        appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail);

        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository, never()).findById(fromUserId);
        verify(userRepository).findById(toUserId);
        verify(appreciationMapper).updateEntityFromDTO(updateDTO, appreciation);
        verify(appreciationRepository).save(appreciation);
    }

    @Test
    void updateAppreciation_WithoutToUserIdUpdate() {
        updateDTO.setToUserId(null);
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        when(userRepository.findByEmail(userEmail)).thenReturn(authenticatedUser);
        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(fromUser));
        doNothing().when(appreciationMapper).updateEntityFromDTO(updateDTO, appreciation);
        when(appreciationRepository.save(appreciation)).thenReturn(appreciation);

        appreciationService.updateAppreciation(appreciationId, updateDTO, userEmail);

        verify(appreciationRepository).findById(appreciationId);
        verify(userRepository).findByEmail(userEmail);
        verify(userRepository).findById(fromUserId);
        verify(userRepository, never()).findById(toUserId);
        verify(appreciationMapper).updateEntityFromDTO(updateDTO, appreciation);
        verify(appreciationRepository).save(appreciation);
    }

    @Test
    void deleteAppreciation_Success() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.of(appreciation));
        doNothing().when(appreciationRepository).deleteById(appreciationId);

        appreciationService.deleteAppreciation(appreciationId);

        verify(appreciationRepository).findById(appreciationId);
        verify(appreciationRepository).deleteById(appreciationId);
    }

    @Test
    void deleteAppreciation_NullId() {
        assertThrows(IllegalArgumentException.class,
                () -> appreciationService.deleteAppreciation(null));
        verify(appreciationRepository, never()).findById(any());
        verify(appreciationRepository, never()).deleteById(any());
    }

    @Test
    void deleteAppreciation_NotFound() {
        when(appreciationRepository.findById(appreciationId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> appreciationService.deleteAppreciation(appreciationId));
        verify(appreciationRepository).findById(appreciationId);
        verify(appreciationRepository, never()).deleteById(any());
    }
}