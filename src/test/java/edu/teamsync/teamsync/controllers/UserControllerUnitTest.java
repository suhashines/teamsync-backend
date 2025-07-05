package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.UserController;
import edu.teamsync.teamsync.dto.userDTO.UserCreationDTO;
import edu.teamsync.teamsync.dto.userDTO.UserResponseDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponseDTO user1;
    private UserResponseDTO user2;
    private UserCreationDTO userCreationDTO;
    private UserUpdateDTO userUpdateDTO;

    @BeforeEach
    void setup() {
        user1 = UserResponseDTO.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .profilePicture("profile1.jpg")
                .designation("Software Engineer")
                .birthdate(LocalDate.of(1990, 5, 15))
                .joinDate(LocalDate.of(2020, 1, 10))
                .predictedBurnoutRisk(false)
                .build();

        user2 = UserResponseDTO.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .profilePicture("profile2.jpg")
                .designation("Product Manager")
                .birthdate(LocalDate.of(1985, 8, 20))
                .joinDate(LocalDate.of(2019, 3, 15))
                .predictedBurnoutRisk(true)
                .build();

        userCreationDTO = UserCreationDTO.builder()
                .name("Alice Johnson")
                .email("alice.johnson@example.com")
                .password("password123")
                .build();

        userUpdateDTO = UserUpdateDTO.builder()
                .name("John Updated")
                .email("john.updated@example.com")
                .profilePicture("updated_profile.jpg")
                .designation("Senior Software Engineer")
                .birthdate(LocalDate.of(1990, 5, 15))
                .joinDate(LocalDate.of(2020, 1, 10))
                .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(userService).createUser(any(UserCreationDTO.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreationDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("User created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService, times(1)).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating user with invalid data")
    void createUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserCreationDTO invalidDTO = new UserCreationDTO();
        // Missing required fields

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating user with blank name")
    void createUser_BlankName_ReturnsBadRequest() throws Exception {
        UserCreationDTO invalidDTO = UserCreationDTO.builder()
                .name("")
                .email("test@example.com")
                .password("password123")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating user with invalid email")
    void createUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserCreationDTO invalidDTO = UserCreationDTO.builder()
                .name("John Doe")
                .email("invalid-email")
                .password("password123")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating user with blank password")
    void createUser_BlankPassword_ReturnsBadRequest() throws Exception {
        UserCreationDTO invalidDTO = UserCreationDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .password("")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should return all users with success response")
    void getAllUsers_ReturnsSuccessResponse() throws Exception {
        List<UserResponseDTO> userList = List.of(user1, user2);

        when(userService.getAllUsers()).thenReturn(userList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("John Doe"))
                .andExpect(jsonPath("$.data[0].email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Jane Smith"))
                .andExpect(jsonPath("$.data[1].email").value("jane.smith@example.com"))
                .andExpect(jsonPath("$.metadata.count").value(2));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void getAllUsers_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<UserResponseDTO> emptyList = List.of();

        when(userService.getAllUsers()).thenReturn(emptyList);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.metadata.count").value(0));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should return user by ID with success response")
    void getUser_ValidId_ReturnsSuccessResponse() throws Exception {
        when(userService.getUser(1L)).thenReturn(user1);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("John Doe"))
                .andExpect(jsonPath("$.data.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.designation").value("Software Engineer"))
                .andExpect(jsonPath("$.data.predictedBurnoutRisk").value(false));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(userService).updateUser(anyLong(), any(UserUpdateDTO.class));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService, times(1)).updateUser(eq(1L), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating user with invalid data")
    void updateUser_InvalidData_ReturnsBadRequest() throws Exception {
        UserUpdateDTO invalidDTO = new UserUpdateDTO();
        // Missing required fields

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating user with blank name")
    void updateUser_BlankName_ReturnsBadRequest() throws Exception {
        UserUpdateDTO invalidDTO = UserUpdateDTO.builder()
                .name("")
                .email("test@example.com")
                .build();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating user with invalid email")
    void updateUser_InvalidEmail_ReturnsBadRequest() throws Exception {
        UserUpdateDTO invalidDTO = UserUpdateDTO.builder()
                .name("John Doe")
                .email("invalid-email")
                .build();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("User deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(userService, times(1)).deleteUser(eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getAllUsers_ServiceException_ReturnsErrorResponse() throws Exception {
        when(userService.getAllUsers()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/users"))
                .andExpect(status().isInternalServerError());

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @DisplayName("Should handle invalid path variable for getUser")
    void getUser_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/users/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updateUser")
    void updateUser_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deleteUser")
    void deleteUser_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/users/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createUser")
    void createUser_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updateUser")
    void updateUser_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createUser")
    void createUser_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updateUser")
    void updateUser_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle null values in user creation")
    void createUser_NullValues_ReturnsBadRequest() throws Exception {
        UserCreationDTO nullDTO = UserCreationDTO.builder()
                .name(null)
                .email(null)
                .password(null)
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should handle null values in user update")
    void updateUser_NullValues_ReturnsBadRequest() throws Exception {
        UserUpdateDTO nullDTO = UserUpdateDTO.builder()
                .name(null)
                .email(null)
                .build();

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nullDTO)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(UserUpdateDTO.class));
    }

    @Test
    @DisplayName("Should handle empty JSON object for createUser")
    void createUser_EmptyJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(UserCreationDTO.class));
    }

    @Test
    @DisplayName("Should handle empty JSON object for updateUser")
    void updateUser_EmptyJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).updateUser(anyLong(), any(UserUpdateDTO.class));
    }
}