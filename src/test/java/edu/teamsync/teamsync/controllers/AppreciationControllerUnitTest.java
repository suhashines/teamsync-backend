package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.AppreciationController;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationResponseDTO;

import edu.teamsync.teamsync.service.AppreciationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZonedDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppreciationController.class)
@AutoConfigureMockMvc(addFilters = false)  // disables Spring Security filters
class AppreciationControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppreciationService appreciationService;

    @Autowired
    private ObjectMapper objectMapper;

    private AppreciationResponseDTO appreciation1;
    private AppreciationResponseDTO appreciation2;

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
    }

    @Test
    @DisplayName("Should return all appreciations with success response")
    void getAllAppreciations_ReturnsSuccessResponse() throws Exception {
        List<AppreciationResponseDTO> appreciationList = List.of(appreciation1, appreciation2);

        when(appreciationService.getAllAppreciations()).thenReturn(appreciationList);

        mockMvc.perform(get("/appreciations")) // Change path if needed
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
}
