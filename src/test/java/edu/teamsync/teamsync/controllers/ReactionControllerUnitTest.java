package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.ReactionController;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.service.ReactionService;
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

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReactionControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReactionService reactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private ReactionResponseDTO reaction1;
    private ReactionResponseDTO reaction2;
    private ReactionCreateRequestDTO createRequestDTO;

    @BeforeEach
    void setup() {
        reaction1 = ReactionResponseDTO.builder()
                .id(1L)
                .userId(10L)
                .reactionType("LIKE")
                .createdAt(ZonedDateTime.now())
                .build();

        reaction2 = ReactionResponseDTO.builder()
                .id(2L)
                .userId(11L)
                .reactionType("LOVE")
                .createdAt(ZonedDateTime.now())
                .build();

        createRequestDTO = new ReactionCreateRequestDTO();
        createRequestDTO.setUserId(10L);
        createRequestDTO.setReactionType("LIKE");
    }

    @Test
    @DisplayName("Should return all reactions for a feedpost with success response")
    void getReactions_ValidFeedpostId_ReturnsSuccessResponse() throws Exception {
        List<ReactionResponseDTO> reactionList = List.of(reaction1, reaction2);

        when(reactionService.getAllReactions(100L)).thenReturn(reactionList);

        mockMvc.perform(get("/feedposts/100/reactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Reactions fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].userId").value(10))
                .andExpect(jsonPath("$.data[0].reactionType").value("LIKE"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].userId").value(11))
                .andExpect(jsonPath("$.data[1].reactionType").value("LOVE"));

        verify(reactionService, times(1)).getAllReactions(100L);
    }

    @Test
    @DisplayName("Should return empty list when no reactions exist for feedpost")
    void getReactions_NoReactions_ReturnsSuccessResponse() throws Exception {
        List<ReactionResponseDTO> emptyList = List.of();

        when(reactionService.getAllReactions(100L)).thenReturn(emptyList);

        mockMvc.perform(get("/feedposts/100/reactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Reactions fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(reactionService, times(1)).getAllReactions(100L);
    }

    @Test
    @DisplayName("Should add reaction successfully")
    void addReaction_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(reactionService).addReaction(anyLong(), any(ReactionCreateRequestDTO.class));

        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Reaction added successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reactionService, times(1)).addReaction(eq(100L), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when adding reaction with invalid data")
    void addReaction_InvalidData_ReturnsBadRequest() throws Exception {
        ReactionCreateRequestDTO invalidDTO = new ReactionCreateRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).addReaction(anyLong(), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when adding reaction with null user ID")
    void addReaction_NullUserId_ReturnsBadRequest() throws Exception {
        ReactionCreateRequestDTO invalidDTO = new ReactionCreateRequestDTO();
        invalidDTO.setUserId(null);
        invalidDTO.setReactionType("LIKE");

        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).addReaction(anyLong(), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when adding reaction with blank reaction type")
    void addReaction_BlankReactionType_ReturnsBadRequest() throws Exception {
        ReactionCreateRequestDTO invalidDTO = new ReactionCreateRequestDTO();
        invalidDTO.setUserId(10L);
        invalidDTO.setReactionType("");

        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).addReaction(anyLong(), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when adding reaction with null reaction type")
    void addReaction_NullReactionType_ReturnsBadRequest() throws Exception {
        ReactionCreateRequestDTO invalidDTO = new ReactionCreateRequestDTO();
        invalidDTO.setUserId(10L);
        invalidDTO.setReactionType(null);

        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).addReaction(anyLong(), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @DisplayName("Should remove reaction successfully")
    void removeReaction_ValidParameters_ReturnsSuccessResponse() throws Exception {
        doNothing().when(reactionService).removeReaction(anyLong(), anyLong(), anyString());

        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("user_id", "10")
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Reaction removed successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(reactionService, times(1)).removeReaction(eq(100L), eq(10L), eq("LIKE"));
    }

    @Test
    @DisplayName("Should return bad request when removing reaction with missing user_id parameter")
    void removeReaction_MissingUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).removeReaction(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should return bad request when removing reaction with missing reaction_type parameter")
    void removeReaction_MissingReactionType_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("user_id", "10"))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).removeReaction(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should return bad request when removing reaction with invalid user_id parameter")
    void removeReaction_InvalidUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("user_id", "invalid")
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isBadRequest());

        verify(reactionService, never()).removeReaction(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getReactions_ServiceException_ReturnsErrorResponse() throws Exception {
        when(reactionService.getAllReactions(100L)).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/feedposts/100/reactions"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for feedpost ID")
    void getReactions_InvalidFeedpostId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/feedposts/invalid/reactions"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for add reaction")
    void addReaction_InvalidFeedpostId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts/invalid/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for remove reaction")
    void removeReaction_InvalidFeedpostId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/invalid/reactions")
                        .param("user_id", "10")
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for add reaction")
    void addReaction_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for add reaction")
    void addReaction_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts/100/reactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle different reaction types")
    void addReaction_DifferentReactionTypes_ReturnsCreatedResponse() throws Exception {
        String[] reactionTypes = {"LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"};

        for (String reactionType : reactionTypes) {
            ReactionCreateRequestDTO dto = new ReactionCreateRequestDTO();
            dto.setUserId(10L);
            dto.setReactionType(reactionType);

            doNothing().when(reactionService).addReaction(anyLong(), any(ReactionCreateRequestDTO.class));

            mockMvc.perform(post("/feedposts/100/reactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                    .andExpect(jsonPath("$.status").value("CREATED"))
                    .andExpect(jsonPath("$.message").value("Reaction added successfully"));
        }

        verify(reactionService, times(reactionTypes.length)).addReaction(eq(100L), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @DisplayName("Should handle boundary values for feedpost ID")
    void getReactions_BoundaryValues_ReturnsSuccessResponse() throws Exception {
        when(reactionService.getAllReactions(Long.MAX_VALUE)).thenReturn(List.of());

        mockMvc.perform(get("/feedposts/" + Long.MAX_VALUE + "/reactions"))
                .andExpect(status().isOk());

        verify(reactionService, times(1)).getAllReactions(Long.MAX_VALUE);
    }

    @Test
    @DisplayName("Should handle boundary values for user ID in remove reaction")
    void removeReaction_BoundaryValues_ReturnsSuccessResponse() throws Exception {
        doNothing().when(reactionService).removeReaction(anyLong(), anyLong(), anyString());

        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("user_id", String.valueOf(Long.MAX_VALUE))
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isOk());

        verify(reactionService, times(1)).removeReaction(eq(100L), eq(Long.MAX_VALUE), eq("LIKE"));
    }

    @Test
    @DisplayName("Should handle special characters in reaction type")
    void removeReaction_SpecialCharactersInReactionType_ReturnsSuccessResponse() throws Exception {
        doNothing().when(reactionService).removeReaction(anyLong(), anyLong(), anyString());

        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("user_id", "10")
                        .param("reaction_type", "CUSTOM_REACTION"))
                .andExpect(status().isOk());

        verify(reactionService, times(1)).removeReaction(eq(100L), eq(10L), eq("CUSTOM_REACTION"));
    }

    @Test
    @DisplayName("Should handle empty reaction type parameter")
    void removeReaction_EmptyReactionType_CallsService() throws Exception {
        doNothing().when(reactionService).removeReaction(anyLong(), anyLong(), anyString());

        mockMvc.perform(delete("/feedposts/100/reactions")
                        .param("user_id", "10")
                        .param("reaction_type", ""))
                .andExpect(status().isOk());

        verify(reactionService, times(1)).removeReaction(eq(100L), eq(10L), eq(""));
    }

    @Test
    @DisplayName("Should handle multiple reactions for same feedpost")
    void getReactions_MultipleReactions_ReturnsAllReactions() throws Exception {
        List<ReactionResponseDTO> multipleReactions = List.of(
                ReactionResponseDTO.builder().id(1L).userId(10L).reactionType("LIKE").createdAt(ZonedDateTime.now()).build(),
                ReactionResponseDTO.builder().id(2L).userId(11L).reactionType("LOVE").createdAt(ZonedDateTime.now()).build(),
                ReactionResponseDTO.builder().id(3L).userId(12L).reactionType("HAHA").createdAt(ZonedDateTime.now()).build(),
                ReactionResponseDTO.builder().id(4L).userId(13L).reactionType("WOW").createdAt(ZonedDateTime.now()).build(),
                ReactionResponseDTO.builder().id(5L).userId(14L).reactionType("SAD").createdAt(ZonedDateTime.now()).build()
        );

        when(reactionService.getAllReactions(100L)).thenReturn(multipleReactions);

        mockMvc.perform(get("/feedposts/100/reactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Reactions fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(5)))
                .andExpect(jsonPath("$.data[0].reactionType").value("LIKE"))
                .andExpect(jsonPath("$.data[1].reactionType").value("LOVE"))
                .andExpect(jsonPath("$.data[2].reactionType").value("HAHA"))
                .andExpect(jsonPath("$.data[3].reactionType").value("WOW"))
                .andExpect(jsonPath("$.data[4].reactionType").value("SAD"));

        verify(reactionService, times(1)).getAllReactions(100L);
    }
}