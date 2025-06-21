package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.UserMapper;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserCreationDTO userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("User data cannot be null");
        }

        // Check if email already exists
        Users existingUser = userRepository.findByEmail(userDto.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        Users user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

//        return userMapper.toResponseDTO(savedUser);
    }

    public List<UserResponseDTO> getAllUsers() {
        List<Users> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        Users user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        return userMapper.toResponseDTO(user);
    }

    public void updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (userUpdateDTO == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        Users existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        // Check if email is being updated to an existing one
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().equals(existingUser.getEmail())) {
            Users userWithEmail = userRepository.findByEmail(userUpdateDTO.getEmail());
            if (userWithEmail != null && !userWithEmail.getId().equals(existingUser.getId())) {
                throw new IllegalArgumentException("Email already exists: " + userUpdateDTO.getEmail());
            }
        }

        // Update the entity with new values
        userMapper.updateUserFromDTO(userUpdateDTO, existingUser);
        userRepository.save(existingUser);

//        return userMapper.toResponseDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        userRepository.deleteById(id);
    }
}