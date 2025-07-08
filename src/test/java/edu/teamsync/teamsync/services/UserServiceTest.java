package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.UserMapper;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final Long userId = 1L;
    private final String userEmail = "test@example.com";
    private final String userName = "Test User";
    private final String rawPassword = "password123";
    private final String encodedPassword = "encodedPassword123";

    private Users user;
    private Users existingUser;
    private UserCreationDTO userCreationDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    void setUp() {
        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .name(userName)
                .password(encodedPassword)
                .build();

        existingUser = Users.builder()
                .id(2L)
                .email("existing@example.com")
                .name("Existing User")
                .password(encodedPassword)
                .build();

        userCreationDTO = new UserCreationDTO();
        userCreationDTO.setEmail(userEmail);
        userCreationDTO.setName(userName);
        userCreationDTO.setPassword(rawPassword);

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setName("Updated User");

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(userId);
        userResponseDTO.setEmail(userEmail);
        userResponseDTO.setName(userName);
    }

    @Test
    void createUser_Success() {
        Users userToSave = Users.builder()
                .email(userEmail)
                .name(userName)
                .password(rawPassword)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(null);
        when(userMapper.toEntity(userCreationDTO)).thenReturn(userToSave);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(Users.class))).thenReturn(user);

        userService.createUser(userCreationDTO);

        verify(userRepository).findByEmail(userEmail);
        verify(userMapper).toEntity(userCreationDTO);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(Users.class));
    }

    @Test
    void createUser_NullUserData() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));

        verifyNoInteractions(userRepository, userMapper, passwordEncoder);
    }

    @Test
    void createUser_EmailAlreadyExists() {
        when(userRepository.findByEmail(userEmail)).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.createUser(userCreationDTO));

        assertEquals("Email already exists: " + userEmail, exception.getMessage());
        verify(userRepository).findByEmail(userEmail);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    void getAllUsers_Success() {
        List<Users> usersList = Collections.singletonList(user);
        List<UserResponseDTO> expectedResponse = Collections.singletonList(userResponseDTO);

        when(userRepository.findAll()).thenReturn(usersList);
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getId());
        assertEquals(userEmail, result.get(0).getEmail());
        assertEquals(userName, result.get(0).getName());

        verify(userRepository).findAll();
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void getAllUsers_EmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
        verifyNoInteractions(userMapper);
    }

    @Test
    void getUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userEmail, result.getEmail());
        assertEquals(userName, result.getName());

        verify(userRepository).findById(userId);
        verify(userMapper).toResponseDTO(user);
    }

    @Test
    void getUser_NullId() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUser(null));

        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void getUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getUser(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(null);
        doNothing().when(userMapper).updateUserFromDTO(userUpdateDTO, user);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(userId, userUpdateDTO);

        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(userUpdateDTO.getEmail());
        verify(userMapper).updateUserFromDTO(userUpdateDTO, user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NullId() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(null, userUpdateDTO));

        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_NullUpdateData() {
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, null));

        verifyNoInteractions(userRepository, userMapper);
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.updateUser(userId, userUpdateDTO));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_EmailAlreadyExistsForDifferentUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userUpdateDTO.getEmail())).thenReturn(existingUser);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userService.updateUser(userId, userUpdateDTO));

        assertEquals("Email already exists: " + userUpdateDTO.getEmail(), exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(userUpdateDTO.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUser_SameEmailForSameUser() {
        // User updating their own email to the same email
        userUpdateDTO.setEmail(userEmail);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        // Remove the following line:
        // when(userRepository.findByEmail(userEmail)).thenReturn(user);
        doNothing().when(userMapper).updateUserFromDTO(userUpdateDTO, user);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(userId, userUpdateDTO);

        verify(userRepository).findById(userId);
        verify(userRepository, never()).findByEmail(userEmail); // Optionally verify it's NOT called
        verify(userMapper).updateUserFromDTO(userUpdateDTO, user);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NoEmailUpdate() {
        userUpdateDTO.setEmail(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUserFromDTO(userUpdateDTO, user);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(userId, userUpdateDTO);

        verify(userRepository).findById(userId);
        verify(userRepository, never()).findByEmail(anyString());
        verify(userMapper).updateUserFromDTO(userUpdateDTO, user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(userId);

        userService.deleteUser(userId);

        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUser_NullId() {
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(null));

        verifyNoInteractions(userRepository);
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteUser(userId));

        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
        verify(userRepository, never()).deleteById(userId);
    }
}