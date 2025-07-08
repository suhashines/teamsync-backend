package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.ChannelController;
import edu.teamsync.teamsync.dto.channelDTO.ChannelRequestDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelResponseDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelUpdateDTO;
import edu.teamsync.teamsync.entity.Channels.ChannelType;
import edu.teamsync.teamsync.service.ChannelService;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChannelController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChannelControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChannelService channelService;

    @Autowired
    private ObjectMapper objectMapper;

    private ChannelResponseDTO channel1;
    private ChannelResponseDTO channel2;
    private ChannelRequestDTO createDTO;
    private ChannelUpdateDTO updateDTO;

    @BeforeEach
    void setup() {
        channel1 = new ChannelResponseDTO(
                1L,
                "General Discussion",
                ChannelType.direct,
                100L,
                List.of(10L, 20L, 30L)
        );

        channel2 = new ChannelResponseDTO(
                2L,
                "Voice Chat",
                ChannelType.group,
                101L,
                List.of(11L, 21L)
        );

        createDTO = new ChannelRequestDTO(
                "New Channel",
                ChannelType.direct,
                100L,
                List.of(10L, 20L)
        );

        updateDTO = new ChannelUpdateDTO(
                "Updated Channel",
                ChannelType.group,
                100L,
                List.of(10L, 20L, 30L)
        );
    }

    @Test
    @DisplayName("Should return all channels with success response")
    void getAllChannels_ReturnsSuccessResponse() throws Exception {
        List<ChannelResponseDTO> channelList = List.of(channel1, channel2);

        when(channelService.getAllChannels()).thenReturn(channelList);

        mockMvc.perform(get("/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Channels retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("General Discussion"))
                .andExpect(jsonPath("$.data[0].type").value("direct"))
                .andExpect(jsonPath("$.data[0].project_id").value(100))
                .andExpect(jsonPath("$.data[0].members", hasSize(3)))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].name").value("Voice Chat"))
                .andExpect(jsonPath("$.data[1].type").value("group"))
                .andExpect(jsonPath("$.data[1].project_id").value(101))
                .andExpect(jsonPath("$.data[1].members", hasSize(2)));
    }

    @Test
    @DisplayName("Should return empty list when no channels exist")
    void getAllChannels_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<ChannelResponseDTO> emptyList = List.of();

        when(channelService.getAllChannels()).thenReturn(emptyList);

        mockMvc.perform(get("/channels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Channels retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should return channel by ID with success response")
    void getChannelById_ValidId_ReturnsSuccessResponse() throws Exception {
        when(channelService.getChannelById(1L)).thenReturn(channel1);

        mockMvc.perform(get("/channels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Channel retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("General Discussion"))
                .andExpect(jsonPath("$.data.type").value("direct"))
                .andExpect(jsonPath("$.data.project_id").value(100))
                .andExpect(jsonPath("$.data.members", hasSize(3)))
                .andExpect(jsonPath("$.data.members[0]").value(10))
                .andExpect(jsonPath("$.data.members[1]").value(20))
                .andExpect(jsonPath("$.data.members[2]").value(30));
    }

    @Test
    @DisplayName("Should create channel successfully")
    void createChannel_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(channelService).createChannel(any(ChannelRequestDTO.class));

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Channel created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(channelService, times(1)).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating channel with invalid data")
    void createChannel_InvalidData_ReturnsBadRequest() throws Exception {
        ChannelRequestDTO invalidDTO = new ChannelRequestDTO(
                null, // Missing required name
                ChannelType.direct,
                100L,
                List.of(10L, 20L)
        );

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating channel with blank name")
    void createChannel_BlankName_ReturnsBadRequest() throws Exception {
        ChannelRequestDTO invalidDTO = new ChannelRequestDTO(
                "",
                ChannelType.direct,
                100L,
                List.of(10L, 20L)
        );

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating channel with null type")
    void createChannel_NullType_ReturnsBadRequest() throws Exception {
        ChannelRequestDTO invalidDTO = new ChannelRequestDTO(
                "Test Channel",
                null,
                100L,
                List.of(10L, 20L)
        );

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating channel with null project ID")
    void createChannel_NullProjectId_ReturnsBadRequest() throws Exception {
        ChannelRequestDTO invalidDTO = new ChannelRequestDTO(
                "Test Channel",
                ChannelType.group,
                null,
                List.of(10L, 20L)
        );

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating channel with null member IDs")
    void createChannel_NullMemberIds_ReturnsBadRequest() throws Exception {
        ChannelRequestDTO invalidDTO = new ChannelRequestDTO(
                "Test Channel",
                ChannelType.direct,
                100L,
                null
        );

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating channel with empty member IDs")
    void createChannel_EmptyMemberIds_ReturnsBadRequest() throws Exception {
        ChannelRequestDTO invalidDTO = new ChannelRequestDTO(
                "Test Channel",
                ChannelType.group,
                100L,
                List.of()
        );

        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).createChannel(any(ChannelRequestDTO.class));
    }

    @Test
    @DisplayName("Should update channel successfully")
    void updateChannel_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(channelService).updateChannel(anyLong(), any(ChannelUpdateDTO.class));

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Channel updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(channelService, times(1)).updateChannel(eq(1L), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating channel with invalid data")
    void updateChannel_InvalidData_ReturnsBadRequest() throws Exception {
        ChannelUpdateDTO invalidDTO = new ChannelUpdateDTO(
                null, // Missing required name
                ChannelType.direct,
                100L,
                List.of(10L, 20L)
        );

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).updateChannel(anyLong(), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating channel with blank name")
    void updateChannel_BlankName_ReturnsBadRequest() throws Exception {
        ChannelUpdateDTO invalidDTO = new ChannelUpdateDTO(
                "",
                ChannelType.group,
                100L,
                List.of(10L, 20L)
        );

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).updateChannel(anyLong(), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating channel with null type")
    void updateChannel_NullType_ReturnsBadRequest() throws Exception {
        ChannelUpdateDTO invalidDTO = new ChannelUpdateDTO(
                "Test Channel",
                null,
                100L,
                List.of(10L, 20L)
        );

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).updateChannel(anyLong(), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating channel with null project ID")
    void updateChannel_NullProjectId_ReturnsBadRequest() throws Exception {
        ChannelUpdateDTO invalidDTO = new ChannelUpdateDTO(
                "Test Channel",
                ChannelType.direct,
                null,
                List.of(10L, 20L)
        );

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).updateChannel(anyLong(), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating channel with null members")
    void updateChannel_NullMembers_ReturnsBadRequest() throws Exception {
        ChannelUpdateDTO invalidDTO = new ChannelUpdateDTO(
                "Test Channel",
                ChannelType.group,
                100L,
                null
        );

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).updateChannel(anyLong(), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating channel with empty members")
    void updateChannel_EmptyMembers_ReturnsBadRequest() throws Exception {
        ChannelUpdateDTO invalidDTO = new ChannelUpdateDTO(
                "Test Channel",
                ChannelType.direct,
                100L,
                List.of()
        );

        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(channelService, never()).updateChannel(anyLong(), any(ChannelUpdateDTO.class));
    }

    @Test
    @DisplayName("Should delete channel successfully")
    void deleteChannel_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(channelService).deleteChannel(anyLong());

        mockMvc.perform(delete("/channels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Channel deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(channelService, times(1)).deleteChannel(eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getAllChannels_ServiceException_ReturnsErrorResponse() throws Exception {
        when(channelService.getAllChannels()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/channels"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getChannelById")
    void getChannelById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/channels/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updateChannel")
    void updateChannel_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/channels/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deleteChannel")
    void deleteChannel_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/channels/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createChannel")
    void createChannel_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updateChannel")
    void updateChannel_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createChannel")
    void createChannel_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/channels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updateChannel")
    void updateChannel_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/channels/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }
}