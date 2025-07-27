package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.authorization.UserAuthorizationService;
import edu.teamsync.teamsync.dto.userDTO.DesignationUpdateDto;
import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;
import edu.teamsync.teamsync.dto.userDTO.UserProjectDTO;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createUser(@Valid @RequestBody UserCreationDTO userDto) {
        userService.createUser(userDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("User created successfully")
//                .data(createdUser)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();

        SuccessResponse<List<UserResponseDTO>> response = SuccessResponse.<List<UserResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Users retrieved successfully")
                .data(users)
                .metadata(Map.of("count", users.size()))
                .build();

        return ResponseEntity.ok(response);
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<SuccessResponse<UserResponseDTO>> getUser(@PathVariable Long id) {
    //     UserResponseDTO user = userService.getUser(id);

    //     SuccessResponse<UserResponseDTO> response = SuccessResponse.<UserResponseDTO>builder()
    //             .code(HttpStatus.OK.value())
    //             .status(HttpStatus.OK)
    //             .message("User retrieved successfully")
    //             .data(user)
    //             .build();

    //     return ResponseEntity.ok(response);
    // }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {

        userService.updateUser(id, userUpdateDTO);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("User updated successfully")
//                .data(updatedUser)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("User deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/designation/{id}")
    @PreAuthorize("@userAuthorizationService.isManager()")
    public ResponseEntity<SuccessResponse<UserResponseDTO>> updateDesignation(@PathVariable Long id, @Valid @RequestBody DesignationUpdateDto dto) {
        UserResponseDTO user = userService.updateDesignation(id, dto);

        SuccessResponse<UserResponseDTO> response = SuccessResponse.<UserResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .data(user)
                .message("Designation updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/projects")
    public ResponseEntity<SuccessResponse<List<UserProjectDTO>>> getCurrentUserProjects() {
        List<UserProjectDTO> userProjects = userService.getCurrentUserProjects();

        SuccessResponse<List<UserProjectDTO>> response = SuccessResponse.<List<UserProjectDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("User projects retrieved successfully")
                .data(userProjects)
                .metadata(Map.of("count", userProjects.size()))
                .build();

        return ResponseEntity.ok(response);
    }
}