package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.FeedPostController;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostCreateRequest;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostResponseDTO;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostUpdateRequest;
import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostWithReactionDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.service.FeedPostService;
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

import java.time.LocalDate;
import java.time.ZonedDateTime;
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

@WebMvcTest(FeedPostController.class)
@AutoConfigureMockMvc(addFilters = false)
class FeedPostControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FeedPostService feedPostService;

    @Autowired
    private ObjectMapper objectMapper;

    private FeedPostResponseDTO feedPost1;
    private FeedPostResponseDTO feedPost2;
    private FeedPostWithReactionDTO feedPostWithReactions;
    private FeedPostCreateRequest createRequest;
    private FeedPostUpdateRequest updateRequest;

    @BeforeEach
    void setup() {
        // Setup test data
        feedPost1 = new FeedPostResponseDTO();
        feedPost1.setId(1L);
        feedPost1.setType(FeedPosts.FeedPostType.text);
        feedPost1.setAuthorId(10L);
        feedPost1.setContent("This is a test post");
        feedPost1.setMediaUrls(new String[]{"https://example.com/image1.jpg"});
        feedPost1.setCreatedAt(ZonedDateTime.now());
        feedPost1.setEventDate(LocalDate.now().plusDays(7));
        feedPost1.setPollOptions(new String[]{"Option 1", "Option 2"});
        feedPost1.setAiGenerated(false);
        feedPost1.setAiSummary("Test summary");

        feedPost2 = new FeedPostResponseDTO();
        feedPost2.setId(2L);
        feedPost2.setType(FeedPosts.FeedPostType.poll);
        feedPost2.setAuthorId(20L);
        feedPost2.setContent("This is a poll post");
        feedPost2.setMediaUrls(new String[]{});
        feedPost2.setCreatedAt(ZonedDateTime.now());
        feedPost2.setEventDate(null);
        feedPost2.setPollOptions(new String[]{"Yes", "No", "Maybe"});
        feedPost2.setAiGenerated(true);
        feedPost2.setAiSummary("AI generated summary");

        // Setup feed post with reactions
        feedPostWithReactions = new FeedPostWithReactionDTO();
        feedPostWithReactions.setId(1L);
        feedPostWithReactions.setType(FeedPosts.FeedPostType.text);
        feedPostWithReactions.setAuthorId(10L);
        feedPostWithReactions.setContent("This is a test post with reactions");
        feedPostWithReactions.setMediaUrls(new String[]{"https://example.com/image1.jpg"});
        feedPostWithReactions.setCreatedAt(ZonedDateTime.now());
        feedPostWithReactions.setEventDate(LocalDate.now().plusDays(7));
        feedPostWithReactions.setPollOptions(new String[]{"Option 1", "Option 2"});
        feedPostWithReactions.setAiGenerated(false);
        feedPostWithReactions.setAiSummary("Test summary");

        ReactionDetailDTO reaction1 = new ReactionDetailDTO();
        ReactionDetailDTO reaction2 = new ReactionDetailDTO();
        feedPostWithReactions.setReactions(List.of(reaction1, reaction2));

        // Setup create request
        createRequest = new FeedPostCreateRequest();
        createRequest.setType(FeedPosts.FeedPostType.text);
        createRequest.setContent("New feed post content");
        createRequest.setMediaUrls(new String[]{"https://example.com/image.jpg"});
        createRequest.setEventDate(LocalDate.now().plusDays(5));
        createRequest.setPollOptions(new String[]{"Option A", "Option B"});

        // Setup update request
        updateRequest = new FeedPostUpdateRequest();
        updateRequest.setType(FeedPosts.FeedPostType.text);
        updateRequest.setAuthorId(10L);
        updateRequest.setContent("Updated feed post content");
        updateRequest.setMediaUrls(new String[]{"https://example.com/updated-image.jpg"});
        updateRequest.setCreatedAt(ZonedDateTime.now());
        updateRequest.setEventDate(LocalDate.now().plusDays(10));
        updateRequest.setPollOptions(new String[]{"Updated Option 1", "Updated Option 2"});
        updateRequest.setIsAiGenerated(true);
        updateRequest.setAiSummary("Updated AI summary");
        updateRequest.setReactions(List.of(new ReactionDetailDTO()));
    }

    @Test
    @DisplayName("Should return all feed posts with success response")
    void getAllFeedPosts_ReturnsSuccessResponse() throws Exception {
        List<FeedPostResponseDTO> feedPosts = List.of(feedPost1, feedPost2);

        when(feedPostService.getAllFeedPosts()).thenReturn(feedPosts);

        mockMvc.perform(get("/feedposts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Feed posts fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].type").value("text"))
                .andExpect(jsonPath("$.data[0].author_id").value(10))
                .andExpect(jsonPath("$.data[0].content").value("This is a test post"))
                .andExpect(jsonPath("$.data[0].ai_generated").value(false))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].type").value("poll"))
                .andExpect(jsonPath("$.data[1].author_id").value(20))
                .andExpect(jsonPath("$.data[1].ai_generated").value(true));
    }

    @Test
    @DisplayName("Should return empty list when no feed posts exist")
    void getAllFeedPosts_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<FeedPostResponseDTO> emptyList = List.of();

        when(feedPostService.getAllFeedPosts()).thenReturn(emptyList);

        mockMvc.perform(get("/feedposts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Feed posts fetched successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should return feed post by ID with success response")
    void getFeedPostById_ValidId_ReturnsSuccessResponse() throws Exception {
        when(feedPostService.getFeedPostById(1L)).thenReturn(feedPostWithReactions);

        mockMvc.perform(get("/feedposts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Feed post fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.type").value("text"))
                .andExpect(jsonPath("$.data.author_id").value(10))
                .andExpect(jsonPath("$.data.content").value("This is a test post with reactions"))
                .andExpect(jsonPath("$.data.reactions", hasSize(2)));
    }

    @Test
    @DisplayName("Should create feed post successfully")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(feedPostService).createFeedPost(any(FeedPostCreateRequest.class), anyString());

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Feed post created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(feedPostService, times(1)).createFeedPost(any(FeedPostCreateRequest.class), eq("user@example.com"));
    }

    @Test
    @DisplayName("Should return bad request when creating feed post with invalid data")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_InvalidData_ReturnsBadRequest() throws Exception {
        FeedPostCreateRequest invalidRequest = new FeedPostCreateRequest();
        // Missing required fields (type and content)

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(feedPostService, never()).createFeedPost(any(FeedPostCreateRequest.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating feed post with null type")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_NullType_ReturnsBadRequest() throws Exception {
        FeedPostCreateRequest invalidRequest = new FeedPostCreateRequest();
        invalidRequest.setType(null);
        invalidRequest.setContent("Valid content");

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(feedPostService, never()).createFeedPost(any(FeedPostCreateRequest.class), anyString());
    }

    @Test
    @DisplayName("Should return bad request when creating feed post with blank content")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_BlankContent_ReturnsBadRequest() throws Exception {
        FeedPostCreateRequest invalidRequest = new FeedPostCreateRequest();
        invalidRequest.setType(FeedPosts.FeedPostType.text);
        invalidRequest.setContent("");

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(feedPostService, never()).createFeedPost(any(FeedPostCreateRequest.class), anyString());
    }

    @Test
    @DisplayName("Should create feed post with minimal required fields")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_MinimalData_ReturnsCreatedResponse() throws Exception {
        FeedPostCreateRequest minimalRequest = new FeedPostCreateRequest();
        minimalRequest.setType(FeedPosts.FeedPostType.text);
        minimalRequest.setContent("Minimal content");

        doNothing().when(feedPostService).createFeedPost(any(FeedPostCreateRequest.class), anyString());

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(minimalRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Feed post created successfully"));

        verify(feedPostService, times(1)).createFeedPost(any(FeedPostCreateRequest.class), eq("user@example.com"));
    }

    @Test
    @DisplayName("Should update feed post successfully")
    void updateFeedPost_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(feedPostService).updateFeedPost(anyLong(), any(FeedPostUpdateRequest.class));

        mockMvc.perform(put("/feedposts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Feed post updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(feedPostService, times(1)).updateFeedPost(eq(1L), any(FeedPostUpdateRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when updating feed post with invalid data")
    void updateFeedPost_InvalidData_ReturnsBadRequest() throws Exception {
        FeedPostUpdateRequest invalidRequest = new FeedPostUpdateRequest();
        // Missing required fields (type and authorId)

        mockMvc.perform(put("/feedposts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(feedPostService, never()).updateFeedPost(anyLong(), any(FeedPostUpdateRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when updating feed post with null type")
    void updateFeedPost_NullType_ReturnsBadRequest() throws Exception {
        FeedPostUpdateRequest invalidRequest = new FeedPostUpdateRequest();
        invalidRequest.setType(null);
        invalidRequest.setAuthorId(10L);
        invalidRequest.setContent("Valid content");

        mockMvc.perform(put("/feedposts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(feedPostService, never()).updateFeedPost(anyLong(), any(FeedPostUpdateRequest.class));
    }

    @Test
    @DisplayName("Should return bad request when updating feed post with null author ID")
    void updateFeedPost_NullAuthorId_ReturnsBadRequest() throws Exception {
        FeedPostUpdateRequest invalidRequest = new FeedPostUpdateRequest();
        invalidRequest.setType(FeedPosts.FeedPostType.text);
        invalidRequest.setAuthorId(null);
        invalidRequest.setContent("Valid content");

        mockMvc.perform(put("/feedposts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(feedPostService, never()).updateFeedPost(anyLong(), any(FeedPostUpdateRequest.class));
    }

    @Test
    @DisplayName("Should delete feed post successfully")
    void deleteFeedPost_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(feedPostService).deleteFeedPost(anyLong());

        mockMvc.perform(delete("/feedposts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Feed post deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(feedPostService, times(1)).deleteFeedPost(eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getAllFeedPosts_ServiceException_ReturnsErrorResponse() throws Exception {
        when(feedPostService.getAllFeedPosts()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/feedposts"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getFeedPostById")
    void getFeedPostById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/feedposts/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updateFeedPost")
    void updateFeedPost_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/feedposts/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deleteFeedPost")
    void deleteFeedPost_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/feedposts/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createFeedPost")
    void createFeedPost_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updateFeedPost")
    void updateFeedPost_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/feedposts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createFeedPost")
    void createFeedPost_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updateFeedPost")
    void updateFeedPost_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/feedposts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle different feed post types correctly")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_DifferentTypes_ReturnsCreatedResponse() throws Exception {
        // Test EVENT type
        FeedPostCreateRequest eventRequest = new FeedPostCreateRequest();
        eventRequest.setType(FeedPosts.FeedPostType.event);
        eventRequest.setContent("Event content");
        eventRequest.setEventDate(LocalDate.now().plusDays(30));

        doNothing().when(feedPostService).createFeedPost(any(FeedPostCreateRequest.class), anyString());

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Feed post created successfully"));

        verify(feedPostService, times(1)).createFeedPost(any(FeedPostCreateRequest.class), eq("user@example.com"));
    }

    @Test
    @DisplayName("Should handle poll type feed post creation")
    @WithMockUser(username = "user@example.com")
    void createFeedPost_PollType_ReturnsCreatedResponse() throws Exception {
        FeedPostCreateRequest pollRequest = new FeedPostCreateRequest();
        pollRequest.setType(FeedPosts.FeedPostType.poll);
        pollRequest.setContent("Poll question");
        pollRequest.setPollOptions(new String[]{"Option 1", "Option 2", "Option 3"});

        doNothing().when(feedPostService).createFeedPost(any(FeedPostCreateRequest.class), anyString());

        mockMvc.perform(post("/feedposts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pollRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Feed post created successfully"));

        verify(feedPostService, times(1)).createFeedPost(any(FeedPostCreateRequest.class), eq("user@example.com"));
    }
}