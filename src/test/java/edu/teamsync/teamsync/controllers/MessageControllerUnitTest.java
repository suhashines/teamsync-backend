
package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.MessageController;
import edu.teamsync.teamsync.dto.messageDTO.MessageCreationDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageResponseDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageUpdateDTO;
import edu.teamsync.teamsync.service.MessageService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    private MessageResponseDTO message1;
    private MessageResponseDTO message2;
    private MessageCreationDTO createDTO;
    private MessageUpdateDTO updateDTO;

    @BeforeEach
    void setup() {
        message1 = new MessageResponseDTO(
                1L,
                10L,
                100L,
                20L,
                "Hello, this is a test message",
                ZonedDateTime.now(),
                null
        );

        message2 = new MessageResponseDTO(
                2L,
                11L,
                100L,
                21L,
                "This is another test message",
                ZonedDateTime.now(),
                1L
        );

        createDTO = new MessageCreationDTO(
                "New message content",
                100L,
                20L,
                null
        );

        updateDTO = new MessageUpdateDTO(
                100L,
                20L,
                "Updated message content"
        );
    }

    @Test
    @DisplayName("Should return all channel messages with success response")
    @WithMockUser(username = "user@example.com")
    void getChannelMessages_ValidChannelId_ReturnsSuccessResponse() throws Exception {
        List<MessageResponseDTO> messageList = List.of(message1, message2);

        when(messageService.getChannelMessages(100L)).thenReturn(messageList);

        mockMvc.perform(get("/channels/100/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Messages fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].sender_id").value(10))
                .andExpect(jsonPath("$.data[0].content").value("Hello, this is a test message"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].sender_id").value(11))
                .andExpect(jsonPath("$.data[1].thread_parent_id").value(1));

        verify(messageService, times(1)).getChannelMessages(100L);
    }

    @Test
    @DisplayName("Should return empty list when no messages exist in channel")
    @WithMockUser(username = "user@example.com")
    void getChannelMessages_EmptyChannel_ReturnsSuccessResponse() throws Exception {
        List<MessageResponseDTO> emptyList = List.of();

        when(messageService.getChannelMessages(100L)).thenReturn(emptyList);

        mockMvc.perform(get("/channels/100/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Messages fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));

        verify(messageService, times(1)).getChannelMessages(100L);
    }

    @Test
    @DisplayName("Should return specific message by ID with success response")
    @WithMockUser(username = "user@example.com")
    void getChannelMessage_ValidIds_ReturnsSuccessResponse() throws Exception {
        when(messageService.getChannelMessage(100L, 1L)).thenReturn(message1);

        mockMvc.perform(get("/channels/100/messages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Message fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.sender_id").value(10))
                .andExpect(jsonPath("$.data.channel_id").value(100))
                .andExpect(jsonPath("$.data.recipient_id").value(20))
                .andExpect(jsonPath("$.data.content").value("Hello, this is a test message"));

        verify(messageService, times(1)).getChannelMessage(100L, 1L);
    }

    @Test
    @DisplayName("Should create message successfully")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(messageService).createChannelMessage(anyLong(), any(MessageCreationDTO.class));

        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Message created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(messageService, times(1)).createChannelMessage(eq(100L), any(MessageCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating message with invalid data")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_InvalidData_ReturnsBadRequest() throws Exception {
        MessageCreationDTO invalidDTO = new MessageCreationDTO(
                null, // blank content
                100L,
                20L,
                null
        );

        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).createChannelMessage(anyLong(), any(MessageCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating message with blank content")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_BlankContent_ReturnsBadRequest() throws Exception {
        MessageCreationDTO invalidDTO = new MessageCreationDTO(
                "", // blank content
                100L,
                20L,
                null
        );

        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).createChannelMessage(anyLong(), any(MessageCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating message with null channel ID")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_NullChannelId_ReturnsBadRequest() throws Exception {
        MessageCreationDTO invalidDTO = new MessageCreationDTO(
                "Valid content",
                null, // null channel ID
                20L,
                null
        );

        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).createChannelMessage(anyLong(), any(MessageCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating message with null recipient ID")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_NullRecipientId_ReturnsBadRequest() throws Exception {
        MessageCreationDTO invalidDTO = new MessageCreationDTO(
                "Valid content",
                100L,
                null, // null recipient ID
                null
        );

        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).createChannelMessage(anyLong(), any(MessageCreationDTO.class));
    }

    @Test
    @DisplayName("Should update message successfully")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(messageService).updateChannelMessage(anyLong(), anyLong(), any(MessageUpdateDTO.class), anyString());

        mockMvc.perform(put("/channels/100/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Message updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(messageService, times(1)).updateChannelMessage(eq(100L), eq(1L), any(MessageUpdateDTO.class), eq("user@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when updating message with invalid data")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_InvalidData_ReturnsBadRequest() throws Exception {
        MessageUpdateDTO invalidDTO = new MessageUpdateDTO(
                null, // null channel ID
                20L,
                "Updated content"
        );

        mockMvc.perform(put("/channels/100/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).updateChannelMessage(anyLong(), anyLong(), any(MessageUpdateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when updating message with blank content")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_BlankContent_ReturnsBadRequest() throws Exception {
        MessageUpdateDTO invalidDTO = new MessageUpdateDTO(
                100L,
                20L,
                "" // blank content
        );

        mockMvc.perform(put("/channels/100/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).updateChannelMessage(anyLong(), anyLong(), any(MessageUpdateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when updating message with null recipient ID")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_NullRecipientId_ReturnsBadRequest() throws Exception {
        MessageUpdateDTO invalidDTO = new MessageUpdateDTO(
                100L,
                null, // null recipient ID
                "Updated content"
        );

        mockMvc.perform(put("/channels/100/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(messageService, never()).updateChannelMessage(anyLong(), anyLong(), any(MessageUpdateDTO.class), anyString());
    }

    @Test
    @DisplayName("Should delete message successfully")
    @WithMockUser(username = "user@example.com")
    void deleteChannelMessage_ValidIds_ReturnsSuccessResponse() throws Exception {
        doNothing().when(messageService).deleteChannelMessage(anyLong(), anyLong());

        mockMvc.perform(delete("/channels/100/messages/1")
                        .with(user("user@example.com")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Message deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(messageService, times(1)).deleteChannelMessage(eq(100L), eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    @WithMockUser(username = "user@example.com")
    void getChannelMessages_ServiceException_ReturnsErrorResponse() throws Exception {
        when(messageService.getChannelMessages(100L)).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/channels/100/messages"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for channel ID")
    @WithMockUser(username = "user@example.com")
    void getChannelMessages_InvalidChannelId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/channels/invalid/messages"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for message ID")
    @WithMockUser(username = "user@example.com")
    void getChannelMessage_InvalidMessageId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/channels/100/messages/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for update message")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/channels/invalid/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for delete message")
    @WithMockUser(username = "user@example.com")
    void deleteChannelMessage_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/channels/100/messages/invalid")
                        .with(user("user@example.com")))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for create message")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for update message")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/channels/100/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for create message")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for update message")
    @WithMockUser(username = "user@example.com")
    void updateChannelMessage_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/channels/100/messages/1")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle create message with thread parent ID")
    @WithMockUser(username = "user@example.com")
    void createChannelMessage_WithThreadParentId_ReturnsCreatedResponse() throws Exception {
        MessageCreationDTO threadDTO = new MessageCreationDTO(
                "Reply to thread",
                100L,
                20L,
                1L // thread parent ID
        );

        doNothing().when(messageService).createChannelMessage(anyLong(), any(MessageCreationDTO.class));

        mockMvc.perform(post("/channels/100/messages")
                        .with(user("user@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(threadDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Message created successfully"));

        verify(messageService, times(1)).createChannelMessage(eq(100L), any(MessageCreationDTO.class));
    }

    @Test
    @DisplayName("Should handle path variable boundary values")
    @WithMockUser(username = "user@example.com")
    void getChannelMessages_BoundaryValues_ReturnsSuccessResponse() throws Exception {
        when(messageService.getChannelMessages(Long.MAX_VALUE)).thenReturn(List.of());

        mockMvc.perform(get("/channels/" + Long.MAX_VALUE + "/messages"))
                .andExpect(status().isOk());

        verify(messageService, times(1)).getChannelMessages(Long.MAX_VALUE);
    }
}