package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.PollVoteController;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteCreationDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteResponseDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteUpdateDTO;
import edu.teamsync.teamsync.service.PollVoteService;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PollVoteController.class)
@AutoConfigureMockMvc(addFilters = false)
class PollVoteControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PollVoteService pollVoteService;

    @Autowired
    private ObjectMapper objectMapper;

    private PollVoteResponseDTO pollVote1;
    private PollVoteResponseDTO pollVote2;
    private PollVoteCreationDTO createDTO;
    private PollVoteUpdateDTO updateDTO;

    @BeforeEach
    void setup() {
        pollVote1 = PollVoteResponseDTO.builder()
                .id(1L)
                .pollId(100L)
                .userId(10L)
                .selectedOption("Option A")
                .build();

        pollVote2 = PollVoteResponseDTO.builder()
                .id(2L)
                .pollId(101L)
                .userId(11L)
                .selectedOption("Option B")
                .build();

        createDTO = new PollVoteCreationDTO();
        createDTO.setPollId(100L);
        createDTO.setSelectedOption("Option A");

        updateDTO = new PollVoteUpdateDTO();
        updateDTO.setPollId(100L);
        updateDTO.setUserId(10L);
        updateDTO.setSelectedOption("Option C");
    }

    @Test
    @DisplayName("Should return all poll votes with success response")
    void getAllPollVotes_ReturnsSuccessResponse() throws Exception {
        List<PollVoteResponseDTO> pollVoteList = List.of(pollVote1, pollVote2);

        when(pollVoteService.getAllPollVotes()).thenReturn(pollVoteList);

        mockMvc.perform(get("/pollvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Poll votes fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].poll_id").value(100))
                .andExpect(jsonPath("$.data[0].user_id").value(10))
                .andExpect(jsonPath("$.data[0].selected_option").value("Option A"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].poll_id").value(101))
                .andExpect(jsonPath("$.data[1].user_id").value(11))
                .andExpect(jsonPath("$.data[1].selected_option").value("Option B"));
    }

    @Test
    @DisplayName("Should return empty list when no poll votes exist")
    void getAllPollVotes_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<PollVoteResponseDTO> emptyList = List.of();

        when(pollVoteService.getAllPollVotes()).thenReturn(emptyList);

        mockMvc.perform(get("/pollvotes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Poll votes fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should return poll vote by ID with success response")
    void getPollVoteById_ValidId_ReturnsSuccessResponse() throws Exception {
        when(pollVoteService.getPollVoteById(1L)).thenReturn(pollVote1);

        mockMvc.perform(get("/pollvotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Poll vote fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.poll_id").value(100))
                .andExpect(jsonPath("$.data.user_id").value(10))
                .andExpect(jsonPath("$.data.selected_option").value("Option A"));
    }


    @Test
    @DisplayName("Should create poll vote successfully")
    @WithMockUser(username = "user@example.com")
    void createPollVote_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(pollVoteService).createPollVote(any(PollVoteCreationDTO.class), anyString());

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Poll vote created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(pollVoteService, times(1)).createPollVote(any(PollVoteCreationDTO.class), eq("user@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when creating poll vote with invalid data")
    void createPollVote_InvalidData_ReturnsBadRequest() throws Exception {
        PollVoteCreationDTO invalidDTO = new PollVoteCreationDTO();
        // Missing required fields

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).createPollVote(any(PollVoteCreationDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating poll vote with null poll ID")
    void createPollVote_NullPollId_ReturnsBadRequest() throws Exception {
        PollVoteCreationDTO invalidDTO = new PollVoteCreationDTO();
        invalidDTO.setPollId(null);
        invalidDTO.setSelectedOption("Option A");

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).createPollVote(any(PollVoteCreationDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating poll vote with blank selected option")
    void createPollVote_BlankSelectedOption_ReturnsBadRequest() throws Exception {
        PollVoteCreationDTO invalidDTO = new PollVoteCreationDTO();
        invalidDTO.setPollId(100L);
        invalidDTO.setSelectedOption("");

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).createPollVote(any(PollVoteCreationDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating poll vote with null selected option")
    void createPollVote_NullSelectedOption_ReturnsBadRequest() throws Exception {
        PollVoteCreationDTO invalidDTO = new PollVoteCreationDTO();
        invalidDTO.setPollId(100L);
        invalidDTO.setSelectedOption(null);

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO))
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).createPollVote(any(PollVoteCreationDTO.class), anyString());
    }

    @Test
    @DisplayName("Should update poll vote successfully")
    void updatePollVote_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(pollVoteService).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Poll vote updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(pollVoteService, times(1)).updatePollVote(eq(1L), any(PollVoteUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating poll vote with invalid data")
    void updatePollVote_InvalidData_ReturnsBadRequest() throws Exception {
        PollVoteUpdateDTO invalidDTO = new PollVoteUpdateDTO();
        // Missing required fields

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating poll vote with null poll ID")
    void updatePollVote_NullPollId_ReturnsBadRequest() throws Exception {
        PollVoteUpdateDTO invalidDTO = new PollVoteUpdateDTO();
        invalidDTO.setPollId(null);
        invalidDTO.setUserId(10L);
        invalidDTO.setSelectedOption("Option C");

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating poll vote with null user ID")
    void updatePollVote_NullUserId_ReturnsBadRequest() throws Exception {
        PollVoteUpdateDTO invalidDTO = new PollVoteUpdateDTO();
        invalidDTO.setPollId(100L);
        invalidDTO.setUserId(null);
        invalidDTO.setSelectedOption("Option C");

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating poll vote with blank selected option")
    void updatePollVote_BlankSelectedOption_ReturnsBadRequest() throws Exception {
        PollVoteUpdateDTO invalidDTO = new PollVoteUpdateDTO();
        invalidDTO.setPollId(100L);
        invalidDTO.setUserId(10L);
        invalidDTO.setSelectedOption("");

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating poll vote with null selected option")
    void updatePollVote_NullSelectedOption_ReturnsBadRequest() throws Exception {
        PollVoteUpdateDTO invalidDTO = new PollVoteUpdateDTO();
        invalidDTO.setPollId(100L);
        invalidDTO.setUserId(10L);
        invalidDTO.setSelectedOption(null);

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(pollVoteService, never()).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));
    }

    @Test
    @DisplayName("Should delete poll vote successfully")
    void deletePollVote_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(pollVoteService).deletePollVote(anyLong());

        mockMvc.perform(delete("/pollvotes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Poll vote deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(pollVoteService, times(1)).deletePollVote(eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getAllPollVotes_ServiceException_ReturnsErrorResponse() throws Exception {
        when(pollVoteService.getAllPollVotes()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/pollvotes"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getPollVoteById")
    void getPollVoteById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/pollvotes/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updatePollVote")
    void updatePollVote_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/pollvotes/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deletePollVote")
    void deletePollVote_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/pollvotes/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createPollVote")
    void createPollVote_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updatePollVote")
    void updatePollVote_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createPollVote")
    void createPollVote_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }")
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updatePollVote")
    void updatePollVote_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exception for getPollVoteById")
    void getPollVoteById_ServiceException_ReturnsErrorResponse() throws Exception {
        when(pollVoteService.getPollVoteById(1L)).thenThrow(new RuntimeException("Poll vote not found"));

        mockMvc.perform(get("/pollvotes/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exception for createPollVote")
    void createPollVote_ServiceException_ReturnsErrorResponse() throws Exception {
        doThrow(new RuntimeException("Creation failed")).when(pollVoteService).createPollVote(any(PollVoteCreationDTO.class), anyString());

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO))
                        .with(user("user@example.com")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exception for updatePollVote")
    void updatePollVote_ServiceException_ReturnsErrorResponse() throws Exception {
        doThrow(new RuntimeException("Update failed")).when(pollVoteService).updatePollVote(anyLong(), any(PollVoteUpdateDTO.class));

        mockMvc.perform(put("/pollvotes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exception for deletePollVote")
    void deletePollVote_ServiceException_ReturnsErrorResponse() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(pollVoteService).deletePollVote(anyLong());

        mockMvc.perform(delete("/pollvotes/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle zero and negative IDs gracefully")
    void testEdgeCaseIds() throws Exception {
        // Test with zero ID
        mockMvc.perform(get("/pollvotes/0"))
                .andExpect(status().isOk());

        // Test with negative ID - this might return 404 or 400 depending on your implementation
        mockMvc.perform(get("/pollvotes/-1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle very large ID values")
    void testLargeIds() throws Exception {
        Long largeId = Long.MAX_VALUE;

        mockMvc.perform(get("/pollvotes/" + largeId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle special characters in URL path")
    void testSpecialCharactersInPath() throws Exception {
        mockMvc.perform(get("/pollvotes/abc123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle concurrent requests simulation")
    void testConcurrentRequestsSimulation() throws Exception {
        // Simulate multiple rapid requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/pollvotes"))
                    .andExpect(status().isOk());
        }
    }

    @Test
    @DisplayName("Should handle null JSON values properly")
    void testNullJsonValues() throws Exception {
        String jsonWithNulls = "{\"pollId\":null,\"selectedOption\":null}";

        mockMvc.perform(post("/pollvotes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithNulls)
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());
    }
}