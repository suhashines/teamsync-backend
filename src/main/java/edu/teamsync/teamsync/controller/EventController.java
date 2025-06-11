
package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.dto.eventsDTO.EventCreationDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventResponseDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventUpdateDTO;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createEvent(@Valid @RequestBody EventCreationDTO requestDto) {
        eventService.createEvent(requestDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Event created successfully")
//                .data(responseDto)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<EventResponseDTO>> getEventById(@PathVariable Long id) {
        EventResponseDTO responseDto = eventService.getEventById(id);

        SuccessResponse<EventResponseDTO> response = SuccessResponse.<EventResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Event retrieved successfully")
                .data(responseDto)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<EventResponseDTO>>> getAllEvents() {
        List<EventResponseDTO> events = eventService.getAllEvents();

        SuccessResponse<List<EventResponseDTO>> response = SuccessResponse.<List<EventResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("All events retrieved successfully")
                .data(events)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateEvent(@PathVariable Long id, @Valid @RequestBody EventUpdateDTO requestDto) {
        eventService.updateEvent(id, requestDto);

        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Event updated successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        SuccessResponse<Void> response = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Event deleted successfully")
                .build();
        return ResponseEntity.ok(response);
    }
}