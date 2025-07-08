package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.AppreciationController;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationCreateDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationResponseDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationUpdateDTO;
import edu.teamsync.teamsync.service.AppreciationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppreciationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppreciationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppreciationService appreciationService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppreciationResponseDTO appreciation1;
    private AppreciationResponseDTO appreciation2;
    private AppreciationCreateDTO createDTO;
    private AppreciationUpdateDTO updateDTO;

    @BeforeEach
    void setup() {
        appreciation1 = new AppreciationResponseDTO();
        appreciation1.setId(1L);
        appreciation1.setParentPostId(100L);
        appreciation1.setFromUserId(10L);
        appreciation1.setFromUserName("Alice");
        appreciation1.setToUserId(20L);
        appreciation1.setToUserName("Bob");
        appreciation1.setMessage("Great job!");
        appreciation1.setTimestamp(ZonedDateTime.now());

        appreciation2 = new AppreciationResponseDTO();
        appreciation2.setId(2L);
        appreciation2.setParentPostId(101L);
        appreciation2.setFromUserId(11L);
        appreciation2.setFromUserName("Carol");
        appreciation2.setToUserId(21L);
        appreciation2.setToUserName("Dave");
        appreciation2.setMessage("Well done!");
        appreciation2.setTimestamp(ZonedDateTime.now());

        createDTO = new AppreciationCreateDTO();
        createDTO.setToUserId(20L);
        createDTO.setMessage("Great work!");

        updateDTO = new AppreciationUpdateDTO();
        updateDTO.setFromUserId(10L);
        updateDTO.setToUserId(20L);
        updateDTO.setMessage("Updated message");
        updateDTO.setTimestamp(ZonedDateTime.now());
    }

    @Test
    @DisplayName("Should return all appreciations with success response")
    void getAllAppreciations_ReturnsSuccessResponse() throws Exception {
        List<AppreciationResponseDTO> appreciationList = List.of(appreciation1, appreciation2);

        when(appreciationService.getAllAppreciations()).thenReturn(appreciationList);

        mockMvc.perform(get("/appreciations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Appreciations retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].from_user_name").value("Alice"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].from_user_name").value("Carol"))
                .andExpect(jsonPath("$.metadata.count").value(2));
    }

    @Test
    @DisplayName("Should return empty list when no appreciations exist")
    @WithMockUser(username = "alice@example.com")
    void getAllAppreciations_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<AppreciationResponseDTO> emptyList = List.of();

        when(appreciationService.getAllAppreciations()).thenReturn(emptyList);

        mockMvc.perform(get("/appreciations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Appreciations retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.metadata.count").value(0));
    }

    @Test
    @DisplayName("Should return appreciation by ID with success response")
    @WithMockUser(username = "alice@example.com")
    void getAppreciationById_ValidId_ReturnsSuccessResponse() throws Exception {
        when(appreciationService.getAppreciationById(1L)).thenReturn(appreciation1);

        mockMvc.perform(get("/appreciations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Appreciation retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.from_user_name").value("Alice"))
                .andExpect(jsonPath("$.data.to_user_name").value("Bob"))
                .andExpect(jsonPath("$.data.message").value("Great job!"));
    }

    @Test
    @DisplayName("Should create appreciation successfully")
    @WithMockUser(username = "alice@example.com")
    void createAppreciation_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(appreciationService).createAppreciation(any(AppreciationCreateDTO.class), anyString());

        mockMvc.perform(post("/appreciations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Appreciation created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(appreciationService, times(1)).createAppreciation(any(AppreciationCreateDTO.class), eq("alice@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when creating appreciation with invalid data")
    @WithMockUser(username = "alice@example.com")
    void createAppreciation_InvalidData_ReturnsBadRequest() throws Exception {
        AppreciationCreateDTO invalidDTO = new AppreciationCreateDTO();
        // Missing required fields

        mockMvc.perform(post("/appreciations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(appreciationService, never()).createAppreciation(any(AppreciationCreateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating appreciation with null toUserId")
    @WithMockUser(username = "alice@example.com")
    void createAppreciation_NullToUserId_ReturnsBadRequest() throws Exception {
        AppreciationCreateDTO invalidDTO = new AppreciationCreateDTO();
        invalidDTO.setToUserId(null);
        invalidDTO.setMessage("Great work!");

        mockMvc.perform(post("/appreciations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(appreciationService, never()).createAppreciation(any(AppreciationCreateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating appreciation with blank message")
    @WithMockUser(username = "alice@example.com")
    void createAppreciation_BlankMessage_ReturnsBadRequest() throws Exception {
        AppreciationCreateDTO invalidDTO = new AppreciationCreateDTO();
        invalidDTO.setToUserId(20L);
        invalidDTO.setMessage("");

        mockMvc.perform(post("/appreciations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(appreciationService, never()).createAppreciation(any(AppreciationCreateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should update appreciation successfully")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(appreciationService).updateAppreciation(anyLong(), any(AppreciationUpdateDTO.class), anyString());

        mockMvc.perform(put("/appreciations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Appreciation updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(appreciationService, times(1)).updateAppreciation(eq(1L), any(AppreciationUpdateDTO.class), eq("alice@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when updating appreciation with invalid data")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_InvalidData_ReturnsBadRequest() throws Exception {
        AppreciationUpdateDTO invalidDTO = new AppreciationUpdateDTO();
        // Missing required fields

        mockMvc.perform(put("/appreciations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(appreciationService, never()).updateAppreciation(anyLong(), any(AppreciationUpdateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when updating appreciation with null fromUserId")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_NullFromUserId_ReturnsBadRequest() throws Exception {
        AppreciationUpdateDTO invalidDTO = new AppreciationUpdateDTO();
        invalidDTO.setFromUserId(null);
        invalidDTO.setToUserId(20L);
        invalidDTO.setMessage("Updated message");
        invalidDTO.setTimestamp(ZonedDateTime.now());

        mockMvc.perform(put("/appreciations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(appreciationService, never()).updateAppreciation(anyLong(), any(AppreciationUpdateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when updating appreciation with blank message")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_BlankMessage_ReturnsBadRequest() throws Exception {
        AppreciationUpdateDTO invalidDTO = new AppreciationUpdateDTO();
        invalidDTO.setFromUserId(10L);
        invalidDTO.setToUserId(20L);
        invalidDTO.setMessage("");
        invalidDTO.setTimestamp(ZonedDateTime.now());

        mockMvc.perform(put("/appreciations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(appreciationService, never()).updateAppreciation(anyLong(), any(AppreciationUpdateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should delete appreciation successfully")
    @WithMockUser(username = "alice@example.com")
    void deleteAppreciation_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(appreciationService).deleteAppreciation(anyLong());

        mockMvc.perform(delete("/appreciations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Appreciation deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(appreciationService, times(1)).deleteAppreciation(eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    @WithMockUser(username = "alice@example.com")
    void getAllAppreciations_ServiceException_ReturnsErrorResponse() throws Exception {
        when(appreciationService.getAllAppreciations()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/appreciations"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getAppreciationById")
    @WithMockUser(username = "alice@example.com")
    void getAppreciationById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/appreciations/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updateAppreciation")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/appreciations/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deleteAppreciation")
    @WithMockUser(username = "alice@example.com")
    void deleteAppreciation_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/appreciations/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createAppreciation")
    @WithMockUser(username = "alice@example.com")
    void createAppreciation_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/appreciations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updateAppreciation")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/appreciations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createAppreciation")
    @WithMockUser(username = "alice@example.com")
    void createAppreciation_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/appreciations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updateAppreciation")
    @WithMockUser(username = "alice@example.com")
    void updateAppreciation_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/appreciations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}