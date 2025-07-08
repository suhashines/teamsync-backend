package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.CommentController;
import edu.teamsync.teamsync.dto.commentDTO.CommentCreateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentResponseDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentUpdateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.ReplyCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.service.CommentService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc
class CommentControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private CommentResponseDTO comment1;
    private CommentResponseDTO comment2;
    private CommentResponseDTO reply1;
    private CommentCreateRequestDTO createDTO;
    private CommentUpdateRequestDTO updateDTO;
    private ReplyCreateRequestDTO replyCreateDTO;
    private ReactionCreateRequestDTO reactionCreateDTO;
    private ReactionResponseDTO reactionResponse;

    @BeforeEach
    void setup() {
        // Setup comment responses
        comment1 = new CommentResponseDTO();
        comment1.setId(1L);
        comment1.setPostId(100L);
        comment1.setAuthorId(10L);
        comment1.setContent("This is a great post!");
        comment1.setTimestamp(ZonedDateTime.now());
        comment1.setParentCommentId(null);
        comment1.setReactions(List.of());
        comment1.setReplyCount(0);

        comment2 = new CommentResponseDTO();
        comment2.setId(2L);
        comment2.setPostId(100L);
        comment2.setAuthorId(20L);
        comment2.setContent("I agree with this!");
        comment2.setTimestamp(ZonedDateTime.now());
        comment2.setParentCommentId(null);
        comment2.setReactions(List.of());
        comment2.setReplyCount(1);

        reply1 = new CommentResponseDTO();
        reply1.setId(3L);
        reply1.setPostId(100L);
        reply1.setAuthorId(30L);
        reply1.setContent("Thanks for the feedback!");
        reply1.setTimestamp(ZonedDateTime.now());
        reply1.setParentCommentId(1L);
        reply1.setReactions(List.of());
        reply1.setReplyCount(0);

        // Setup DTOs
        createDTO = new CommentCreateRequestDTO();
        createDTO.setPostId(100L);
        createDTO.setContent("This is a new comment");
        createDTO.setParentCommentId(null);

        updateDTO = new CommentUpdateRequestDTO();
        updateDTO.setPostId(100L);
        updateDTO.setAuthorId(10L);
        updateDTO.setContent("This is an updated comment");
        updateDTO.setTimestamp(ZonedDateTime.now());
        updateDTO.setParentCommentId(null);
        updateDTO.setReactions(List.of());
        updateDTO.setReplyCount(0);

        replyCreateDTO = new ReplyCreateRequestDTO();
        replyCreateDTO.setContent("This is a reply");
        replyCreateDTO.setAuthor_id(30L);

        reactionCreateDTO = new ReactionCreateRequestDTO();
        reactionCreateDTO.setUserId(40L);
        reactionCreateDTO.setReactionType("LIKE");

        reactionResponse = new ReactionResponseDTO();
        reactionResponse.setId(1L);
        reactionResponse.setUserId(40L);
        reactionResponse.setReactionType("LIKE");
        reactionResponse.setCreatedAt(ZonedDateTime.now());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return all comments for a post with success response")
    void getAllComments_ReturnsSuccessResponse() throws Exception {
        List<CommentResponseDTO> comments = List.of(comment1, comment2);

        when(commentService.getAllCommentsByPostId(100L)).thenReturn(comments);

        mockMvc.perform(get("/feedposts/100/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All Comments fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].content").value("This is a great post!"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].content").value("I agree with this!"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return empty list when no comments exist")
    void getAllComments_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<CommentResponseDTO> emptyList = List.of();

        when(commentService.getAllCommentsByPostId(100L)).thenReturn(emptyList);

        mockMvc.perform(get("/feedposts/100/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All Comments fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return comment by ID with success response")
    void getComment_ValidId_ReturnsSuccessResponse() throws Exception {
        when(commentService.getCommentById(100L, 1L)).thenReturn(comment1);

        mockMvc.perform(get("/feedposts/100/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Comment fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.content").value("This is a great post!"))
                .andExpect(jsonPath("$.data.post_id").value(100))
                .andExpect(jsonPath("$.data.author_id").value(10));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should create comment successfully")
    void createComment_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(commentService).createComment(eq(100L), any(CommentCreateRequestDTO.class), anyString());

        mockMvc.perform(post("/feedposts/100/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Comment created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(commentService, times(1)).createComment(eq(100L), any(CommentCreateRequestDTO.class), eq("test@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when creating comment with invalid data")
    void createComment_InvalidData_ReturnsBadRequest() throws Exception {
        CommentCreateRequestDTO invalidDTO = new CommentCreateRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/feedposts/100/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(anyLong(), any(CommentCreateRequestDTO.class), anyString());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when creating comment with null postId")
    void createComment_NullPostId_ReturnsBadRequest() throws Exception {
        CommentCreateRequestDTO invalidDTO = new CommentCreateRequestDTO();
        invalidDTO.setPostId(null);
        invalidDTO.setContent("This is a comment");

        mockMvc.perform(post("/feedposts/100/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(anyLong(), any(CommentCreateRequestDTO.class), anyString());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when creating comment with blank content")
    void createComment_BlankContent_ReturnsBadRequest() throws Exception {
        CommentCreateRequestDTO invalidDTO = new CommentCreateRequestDTO();
        invalidDTO.setPostId(100L);
        invalidDTO.setContent("");

        mockMvc.perform(post("/feedposts/100/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).createComment(anyLong(), any(CommentCreateRequestDTO.class), anyString());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should update comment successfully")
    void updateComment_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(commentService).updateComment(eq(100L), eq(1L), any(CommentUpdateRequestDTO.class));

        mockMvc.perform(put("/feedposts/100/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Comment updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(commentService, times(1)).updateComment(eq(100L), eq(1L), any(CommentUpdateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when updating comment with invalid data")
    void updateComment_InvalidData_ReturnsBadRequest() throws Exception {
        CommentUpdateRequestDTO invalidDTO = new CommentUpdateRequestDTO();
        // Missing required fields

        mockMvc.perform(put("/feedposts/100/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateComment(anyLong(), anyLong(), any(CommentUpdateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should delete comment successfully")
    void deleteComment_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(commentService).deleteComment(100L, 1L);

        mockMvc.perform(delete("/feedposts/100/comments/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Comment deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(commentService, times(1)).deleteComment(eq(100L), eq(1L));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return all comment reactions with success response")
    void getAllCommentReactions_ReturnsSuccessResponse() throws Exception {
        List<ReactionResponseDTO> reactions = List.of(reactionResponse);

        when(commentService.getAllReactionsByCommentId(100L, 1L)).thenReturn(reactions);

        mockMvc.perform(get("/feedposts/100/comments/1/reactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Comment reactions fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].reactionType").value("LIKE"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should add reaction to comment successfully")
    void addCommentReaction_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(commentService).addReactionToComment(eq(100L), eq(1L), any(ReactionCreateRequestDTO.class));

        mockMvc.perform(post("/feedposts/100/comments/1/reactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactionCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Reaction added to comment successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(commentService, times(1)).addReactionToComment(eq(100L), eq(1L), any(ReactionCreateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should remove reaction from comment successfully")
    void removeCommentReaction_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(commentService).removeReactionFromComment(100L, 1L, 40L, "LIKE");

        mockMvc.perform(delete("/feedposts/100/comments/1/reactions")
                        .with(csrf())
                        .param("user_id", "40")
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Reaction removed from comment successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(commentService, times(1)).removeReactionFromComment(eq(100L), eq(1L), eq(40L), eq("LIKE"));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return all replies with success response")
    void getAllReplies_ReturnsSuccessResponse() throws Exception {
        List<CommentResponseDTO> replies = List.of(reply1);

        when(commentService.getAllRepliesByCommentId(100L, 1L)).thenReturn(replies);

        mockMvc.perform(get("/feedposts/100/comments/1/replies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All replies fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(3))
                .andExpect(jsonPath("$.data[0].content").value("Thanks for the feedback!"))
                .andExpect(jsonPath("$.data[0].parent_comment_id").value(1));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should add reply to comment successfully")
    void addReplyToComment_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(commentService).addReplyToComment(eq(100L), eq(1L), any(ReplyCreateRequestDTO.class));

        mockMvc.perform(post("/feedposts/100/comments/1/replies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(replyCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Reply added to comment successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(commentService, times(1)).addReplyToComment(eq(100L), eq(1L), any(ReplyCreateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when adding reply with invalid data")
    void addReplyToComment_InvalidData_ReturnsBadRequest() throws Exception {
        ReplyCreateRequestDTO invalidDTO = new ReplyCreateRequestDTO();
        // Missing required fields

        mockMvc.perform(post("/feedposts/100/comments/1/replies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).addReplyToComment(anyLong(), anyLong(), any(ReplyCreateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when adding reply with blank content")
    void addReplyToComment_BlankContent_ReturnsBadRequest() throws Exception {
        ReplyCreateRequestDTO invalidDTO = new ReplyCreateRequestDTO();
        invalidDTO.setContent("");
        invalidDTO.setAuthor_id(30L);

        mockMvc.perform(post("/feedposts/100/comments/1/replies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).addReplyToComment(anyLong(), anyLong(), any(ReplyCreateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should return bad request when adding reply with null author_id")
    void addReplyToComment_NullAuthorId_ReturnsBadRequest() throws Exception {
        ReplyCreateRequestDTO invalidDTO = new ReplyCreateRequestDTO();
        invalidDTO.setContent("This is a reply");
        invalidDTO.setAuthor_id(null);

        mockMvc.perform(post("/feedposts/100/comments/1/replies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).addReplyToComment(anyLong(), anyLong(), any(ReplyCreateRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle service exceptions gracefully")
    void getAllComments_ServiceException_ReturnsErrorResponse() throws Exception {
        when(commentService.getAllCommentsByPostId(100L)).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/feedposts/100/comments"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle invalid path variable for getComment")
    void getComment_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/feedposts/100/comments/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle invalid path variable for updateComment")
    void updateComment_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/feedposts/100/comments/invalid")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle invalid path variable for deleteComment")
    void deleteComment_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/100/comments/invalid")
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle missing request body for createComment")
    void createComment_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts/100/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle missing request body for updateComment")
    void updateComment_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/feedposts/100/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle malformed JSON for createComment")
    void createComment_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts/100/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle malformed JSON for updateComment")
    void updateComment_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/feedposts/100/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com")
    @DisplayName("Should handle invalid user_id parameter for removeCommentReaction")
    void removeCommentReaction_InvalidUserId_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/100/comments/1/reactions")
                        .with(csrf())
                        .param("user_id", "invalid")
                        .param("reaction_type", "LIKE"))
                .andExpect(status().isBadRequest());
    }
}