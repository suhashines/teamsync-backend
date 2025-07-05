package edu.teamsync.teamsync.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.teamsync.teamsync.controller.EventController;
import edu.teamsync.teamsync.dto.eventsDTO.EventCreationDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventResponseDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventUpdateDTO;
import edu.teamsync.teamsync.entity.Events.EventType;
import edu.teamsync.teamsync.service.EventService;
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

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private EventResponseDTO event1;
    private EventResponseDTO event2;
    private EventCreationDTO createDTO;
    private EventUpdateDTO updateDTO;

    @BeforeEach
    void setup() {
        event1 = new EventResponseDTO(
                1L,
                "Birthday Meeting",
                "Birthday celebration meeting",
                EventType.Birthday,
                LocalDate.of(2024, 12, 15),
                List.of(10L, 20L, 30L),
                LocalDate.of(2024, 12, 10)
        );

        event2 = new EventResponseDTO(
                2L,
                "Outing somewhere",
                "Outing a place",
                EventType.Outing,
                LocalDate.of(2024, 12, 20),
                List.of(15L, 25L),
                LocalDate.of(2024, 12, 15)
        );

        createDTO = new EventCreationDTO(
                "New Event",
                "Description for new event",
                EventType.Outing,
                LocalDate.of(2024, 12, 25),
                List.of(40L, 50L)
        );

        updateDTO = new EventUpdateDTO(
                "Updated Event",
                "Updated description",
                EventType.Outing,
                LocalDate.of(2024, 12, 30),
                List.of(60L, 70L)
        );
    }

    @Test
    @DisplayName("Should return all events with success response")
    void getAllEvents_ReturnsSuccessResponse() throws Exception {
        List<EventResponseDTO> eventList = List.of(event1, event2);

        when(eventService.getAllEvents()).thenReturn(eventList);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All events retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].title").value("Birthday Meeting"))
                .andExpect(jsonPath("$.data[0].type").value("Birthday"))
                .andExpect(jsonPath("$.data[1].id").value(2))
                .andExpect(jsonPath("$.data[1].title").value("Outing somewhere"))
                .andExpect(jsonPath("$.data[1].type").value("Outing"));
    }

    @Test
    @DisplayName("Should return empty list when no events exist")
    void getAllEvents_EmptyList_ReturnsSuccessResponse() throws Exception {
        List<EventResponseDTO> emptyList = List.of();

        when(eventService.getAllEvents()).thenReturn(emptyList);

        mockMvc.perform(get("/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("All events retrieved successfully"))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    @DisplayName("Should return event by ID with success response")
    void getEventById_ValidId_ReturnsSuccessResponse() throws Exception {
        when(eventService.getEventById(1L)).thenReturn(event1);

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Event retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Birthday Meeting"))
                .andExpect(jsonPath("$.data.description").value("Birthday celebration meeting"))
                .andExpect(jsonPath("$.data.type").value("Birthday"))
                .andExpect(jsonPath("$.data.date").value("2024-12-15"))
                .andExpect(jsonPath("$.data.participant_ids", hasSize(3)))
                .andExpect(jsonPath("$.data.participant_ids[0]").value(10))
                .andExpect(jsonPath("$.data.participant_ids[1]").value(20))
                .andExpect(jsonPath("$.data.participant_ids[2]").value(30))
                .andExpect(jsonPath("$.data.tentative_starting_date").value("2024-12-10"));
    }

    @Test
    @DisplayName("Should create event successfully")
    void createEvent_ValidData_ReturnsCreatedResponse() throws Exception {
        doNothing().when(eventService).createEvent(any(EventCreationDTO.class));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.message").value("Event created successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(eventService, times(1)).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating event with invalid data")
    void createEvent_InvalidData_ReturnsBadRequest() throws Exception {
        EventCreationDTO invalidDTO = new EventCreationDTO(
                "",  // blank title
                "Description",
                EventType.Birthday,
                LocalDate.of(2024, 12, 25),
                List.of(40L, 50L)
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating event with null title")
    void createEvent_NullTitle_ReturnsBadRequest() throws Exception {
        EventCreationDTO invalidDTO = new EventCreationDTO(
                null,  // null title
                "Description",
                EventType.Outing,
                LocalDate.of(2024, 12, 25),
                List.of(40L, 50L)
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating event with null type")
    void createEvent_NullType_ReturnsBadRequest() throws Exception {
        EventCreationDTO invalidDTO = new EventCreationDTO(
                "Event Title",
                "Description",
                null,  // null type
                LocalDate.of(2024, 12, 25),
                List.of(40L, 50L)
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating event with null date")
    void createEvent_NullDate_ReturnsBadRequest() throws Exception {
        EventCreationDTO invalidDTO = new EventCreationDTO(
                "Event Title",
                "Description",
                EventType.Outing,
                null,  // null date
                List.of(40L, 50L)
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating event with null participant IDs")
    void createEvent_NullParticipantIds_ReturnsBadRequest() throws Exception {
        EventCreationDTO invalidDTO = new EventCreationDTO(
                "Event Title",
                "Description",
                EventType.Outing,
                LocalDate.of(2024, 12, 25),
                null  // null participant IDs
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when creating event with empty participant IDs")
    void createEvent_EmptyParticipantIds_ReturnsBadRequest() throws Exception {
        EventCreationDTO invalidDTO = new EventCreationDTO(
                "Event Title",
                "Description",
                EventType.Birthday,
                LocalDate.of(2024, 12, 25),
                List.of()  // empty participant IDs
        );

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).createEvent(any(EventCreationDTO.class));
    }

    @Test
    @DisplayName("Should update event successfully")
    void updateEvent_ValidData_ReturnsSuccessResponse() throws Exception {
        doNothing().when(eventService).updateEvent(anyLong(), any(EventUpdateDTO.class));

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Event updated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(eventService, times(1)).updateEvent(eq(1L), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating event with invalid data")
    void updateEvent_InvalidData_ReturnsBadRequest() throws Exception {
        EventUpdateDTO invalidDTO = new EventUpdateDTO(
                "",  // blank title
                "Description",
                EventType.Outing,
                LocalDate.of(2024, 12, 30),
                List.of(60L, 70L)
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(anyLong(), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating event with null title")
    void updateEvent_NullTitle_ReturnsBadRequest() throws Exception {
        EventUpdateDTO invalidDTO = new EventUpdateDTO(
                null,  // null title
                "Description",
                EventType.Outing,
                LocalDate.of(2024, 12, 30),
                List.of(60L, 70L)
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(anyLong(), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating event with null type")
    void updateEvent_NullType_ReturnsBadRequest() throws Exception {
        EventUpdateDTO invalidDTO = new EventUpdateDTO(
                "Event Title",
                "Description",
                null,  // null type
                LocalDate.of(2024, 12, 30),
                List.of(60L, 70L)
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(anyLong(), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating event with null date")
    void updateEvent_NullDate_ReturnsBadRequest() throws Exception {
        EventUpdateDTO invalidDTO = new EventUpdateDTO(
                "Event Title",
                "Description",
                EventType.Outing,
                null,  // null date
                List.of(60L, 70L)
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(anyLong(), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating event with null participants")
    void updateEvent_NullParticipants_ReturnsBadRequest() throws Exception {
        EventUpdateDTO invalidDTO = new EventUpdateDTO(
                "Event Title",
                "Description",
                EventType.Outing,
                LocalDate.of(2024, 12, 30),
                null  // null participants
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(anyLong(), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should return bad request when updating event with empty participants")
    void updateEvent_EmptyParticipants_ReturnsBadRequest() throws Exception {
        EventUpdateDTO invalidDTO = new EventUpdateDTO(
                "Event Title",
                "Description",
                EventType.Outing,
                LocalDate.of(2024, 12, 30),
                List.of()  // empty participants
        );

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(eventService, never()).updateEvent(anyLong(), any(EventUpdateDTO.class));
    }

    @Test
    @DisplayName("Should delete event successfully")
    void deleteEvent_ValidId_ReturnsSuccessResponse() throws Exception {
        doNothing().when(eventService).deleteEvent(anyLong());

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("Event deleted successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(eventService, times(1)).deleteEvent(eq(1L));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void getAllEvents_ServiceException_ReturnsErrorResponse() throws Exception {
        when(eventService.getAllEvents()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/events"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle invalid path variable for getEventById")
    void getEventById_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/events/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for updateEvent")
    void updateEvent_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/events/invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle invalid path variable for deleteEvent")
    void deleteEvent_InvalidPathVariable_ReturnsBadRequest() throws Exception {
        mockMvc.perform(delete("/events/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for createEvent")
    void createEvent_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle missing request body for updateEvent")
    void updateEvent_MissingRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for createEvent")
    void createEvent_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle malformed JSON for updateEvent")
    void updateEvent_MalformedJSON_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should handle service exception for getEventById")
    void getEventById_ServiceException_ReturnsErrorResponse() throws Exception {
        when(eventService.getEventById(1L)).thenThrow(new RuntimeException("Event not found"));

        mockMvc.perform(get("/events/1"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exception for createEvent")
    void createEvent_ServiceException_ReturnsErrorResponse() throws Exception {
        doThrow(new RuntimeException("Creation failed")).when(eventService).createEvent(any(EventCreationDTO.class));

        mockMvc.perform(post("/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exception for updateEvent")
    void updateEvent_ServiceException_ReturnsErrorResponse() throws Exception {
        doThrow(new RuntimeException("Update failed")).when(eventService).updateEvent(anyLong(), any(EventUpdateDTO.class));

        mockMvc.perform(put("/events/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle service exception for deleteEvent")
    void deleteEvent_ServiceException_ReturnsErrorResponse() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(eventService).deleteEvent(anyLong());

        mockMvc.perform(delete("/events/1"))
                .andExpect(status().isInternalServerError());
    }
}