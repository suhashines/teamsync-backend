package edu.teamsync.teamsync.service;
import java.util.stream.Collectors;
import edu.teamsync.teamsync.dto.userDTO.DesignationUpdateDto;
import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.dto.userDTO.UserProjectDTO;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.entity.ProjectMembers;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.exception.http.UnauthorizedException;
import edu.teamsync.teamsync.mapper.UserMapper;
import edu.teamsync.teamsync.mapper.ProjectMapper;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.repository.ProjectMemberRepository;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.dto.projectDTO.ProjectDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectMapper projectMapper;

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

    public Users getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found with email: " + email);
        }
        return user ;
    }

    public UserResponseDTO updateDesignation(Long id, DesignationUpdateDto dto) {
     
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));

        if ("manager".equalsIgnoreCase(user.getDesignation())) {
            throw new UnauthorizedException("Cannot update designation for a manager user.");
        }

        user.setDesignation(dto.getDesignation());
        userRepository.save(user);
        return userMapper.toResponseDTO(user);
    }

    /**
     * Check if the current authenticated user has manager designation
     * @return true if current user has manager designation, false otherwise
     */
    public boolean isCurrentUserManager() {
        Users currentUser = getCurrentUser();
        return "manager".equalsIgnoreCase(currentUser.getDesignation());
    }

    /**
     * Get the designation of the current authenticated user
     * @return the designation of the current user, or null if not set
     */
    public String getCurrentUserDesignation() {
        Users currentUser = getCurrentUser();
        return currentUser.getDesignation();
    }

    /**
     * Get all projects that the current user is a member of
     * @return List of UserProjectDTO containing project details and user's role
     */
    public List<UserProjectDTO> getCurrentUserProjects() {
        Users currentUser = getCurrentUser();
        
        // Get all project memberships for the current user
        List<ProjectMembers> userMemberships = projectMemberRepository.findByUserId(currentUser.getId());
        
        List<UserProjectDTO> userProjects = userMemberships.stream()
            .map(membership -> {
                Projects project = membership.getProject();
                ProjectDTO projectDTO = projectMapper.toDto(project);

                return UserProjectDTO.builder()
                        .project(projectDTO)
                        .userRole(membership.getRole().name())
                        .build();
            })
            .collect(Collectors.toList());
        
        
        return userProjects;
    }
}
