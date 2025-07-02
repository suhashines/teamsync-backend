package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.eventsDTO.EventCreationDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventResponseDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventUpdateDTO;
import edu.teamsync.teamsync.entity.Events;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.EventMapper;
import edu.teamsync.teamsync.repository.EventRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private final Long eventId = 1L;
    private final Long userId1 = 1L;
    private final Long userId2 = 2L;
    private final String eventTitle = "Team Meeting";
    private final String eventDescription = "Weekly team sync meeting";
    private final LocalDate eventDate = LocalDate.of(2024, 12, 15);

    private Events event;
    private EventCreationDTO eventCreationDTO;
    private EventUpdateDTO eventUpdateDTO;
    private EventResponseDTO eventResponseDTO;
    private List<Long> participantIds;

    @BeforeEach
    void setUp() {
        participantIds = Arrays.asList(userId1, userId2);

        event = Events.builder()
                .id(eventId)
                .title(eventTitle)
                .description(eventDescription)
                .type(Events.EventType.Birthday)
                .date(eventDate)
                .build();

        eventCreationDTO = new EventCreationDTO(
                eventTitle,
                eventDescription,
                Events.EventType.Birthday,
                eventDate,
                participantIds
        );

        eventUpdateDTO = new EventUpdateDTO(
                "Updated Birthday Title",
                "Updated description",
                Events.EventType.Birthday,
                eventDate.plusDays(1),
                participantIds
        );

        eventResponseDTO = new EventResponseDTO(
                eventId,
                eventTitle,
                eventDescription,
                Events.EventType.Birthday,
                eventDate,
                participantIds,
                eventDate
        );
    }

    @Test
    void createEvent_Success() {
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(true);
        when(eventMapper.toEntity(eventCreationDTO)).thenReturn(event);
        when(eventRepository.save(any(Events.class))).thenReturn(event);

        assertDoesNotThrow(() -> eventService.createEvent(eventCreationDTO));

        verify(userRepository).existsById(userId1);
        verify(userRepository).existsById(userId2);
        verify(eventMapper).toEntity(eventCreationDTO);
        verify(eventRepository).save(event);
    }

    @Test
    void createEvent_ParticipantNotFound() {
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.createEvent(eventCreationDTO));

        verify(userRepository).existsById(userId1);
        verify(userRepository).existsById(userId2);
//        verify(eventMapper, never()).toEntity(any());
        verify(eventMapper, never()).toEntity(any(EventCreationDTO.class));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_FirstParticipantNotFound() {
        when(userRepository.existsById(userId1)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.createEvent(eventCreationDTO));

        verify(userRepository).existsById(userId1);
        verify(userRepository, never()).existsById(userId2);
//        verify(eventMapper, never()).toEntity(any());
        verify(eventMapper, never()).toEntity(any(EventCreationDTO.class));
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_EmptyParticipantsList() {
        EventCreationDTO emptyParticipantsDTO = new EventCreationDTO(
                eventTitle,
                eventDescription,
                Events.EventType.Birthday,
                eventDate,
                Collections.emptyList()
        );

        when(eventMapper.toEntity(emptyParticipantsDTO)).thenReturn(event);
        when(eventRepository.save(any(Events.class))).thenReturn(event);

        assertDoesNotThrow(() -> eventService.createEvent(emptyParticipantsDTO));

        verify(userRepository, never()).existsById(any());
        verify(eventMapper).toEntity(emptyParticipantsDTO);
        verify(eventRepository).save(event);
    }

    @Test
    void getEventById_Success() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDto(event)).thenReturn(eventResponseDTO);

        EventResponseDTO result = eventService.getEventById(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.id());
        assertEquals(eventTitle, result.title());
        assertEquals(eventDescription, result.description());

        verify(eventRepository).findById(eventId);
        verify(eventMapper).toDto(event);
    }

    @Test
    void getEventById_EventNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.getEventById(eventId));

        verify(eventRepository).findById(eventId);
        verify(eventMapper, never()).toDto(any());
    }

    @Test
    void getEventById_NullId() {
        assertThrows(IllegalArgumentException.class, () -> eventService.getEventById(null));

        verify(eventRepository, never()).findById(any());
        verify(eventMapper, never()).toDto(any());
    }

    @Test
    void getAllEvents_Success() {
        List<Events> eventsList = Arrays.asList(event);
        when(eventRepository.findAll()).thenReturn(eventsList);
        when(eventMapper.toDto(event)).thenReturn(eventResponseDTO);

        List<EventResponseDTO> result = eventService.getAllEvents();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(eventId, result.get(0).id());

        verify(eventRepository).findAll();
        verify(eventMapper).toDto(event);
    }

    @Test
    void getAllEvents_EmptyList() {
        when(eventRepository.findAll()).thenReturn(Collections.emptyList());

        List<EventResponseDTO> result = eventService.getAllEvents();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll();
        verify(eventMapper, never()).toDto(any());
    }

    @Test
    void updateEvent_Success() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(true);
        when(eventRepository.save(any(Events.class))).thenReturn(event);

        assertDoesNotThrow(() -> eventService.updateEvent(eventId, eventUpdateDTO));

        verify(eventRepository).findById(eventId);
        verify(userRepository).existsById(userId1);
        verify(userRepository).existsById(userId2);
        verify(eventMapper).updateEventFromDTO(eventUpdateDTO, event);
        verify(eventRepository).save(event);
    }

    @Test
    void updateEvent_EventNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> eventService.updateEvent(eventId, eventUpdateDTO));

        verify(eventRepository).findById(eventId);
        verify(userRepository, never()).existsById(any());
        verify(eventMapper, never()).updateEventFromDTO(any(), any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_NullId() {
        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(null, eventUpdateDTO));

        verify(eventRepository, never()).findById(any());
        verify(userRepository, never()).existsById(any());
        verify(eventMapper, never()).updateEventFromDTO(any(), any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_NullRequestDto() {
        assertThrows(IllegalArgumentException.class, () -> eventService.updateEvent(eventId, null));

        verify(eventRepository, never()).findById(any());
        verify(userRepository, never()).existsById(any());
        verify(eventMapper, never()).updateEventFromDTO(any(), any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_ParticipantNotFound() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(userRepository.existsById(userId1)).thenReturn(true);
        when(userRepository.existsById(userId2)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.updateEvent(eventId, eventUpdateDTO));

        verify(eventRepository).findById(eventId);
        verify(userRepository).existsById(userId1);
        verify(userRepository).existsById(userId2);
        verify(eventMapper, never()).updateEventFromDTO(any(), any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_NullParticipants() {
        EventUpdateDTO nullParticipantsDTO = new EventUpdateDTO(
                "Updated Title",
                "Updated description",
                Events.EventType.Birthday,
                eventDate,
                null
        );

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Events.class))).thenReturn(event);

        assertDoesNotThrow(() -> eventService.updateEvent(eventId, nullParticipantsDTO));

        verify(eventRepository).findById(eventId);
        verify(userRepository, never()).existsById(any());
        verify(eventMapper).updateEventFromDTO(nullParticipantsDTO, event);
        verify(eventRepository).save(event);
    }

    @Test
    void deleteEvent_Success() {
        when(eventRepository.existsById(eventId)).thenReturn(true);

        assertDoesNotThrow(() -> eventService.deleteEvent(eventId));

        verify(eventRepository).existsById(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void deleteEvent_EventNotFound() {
        when(eventRepository.existsById(eventId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> eventService.deleteEvent(eventId));

        verify(eventRepository).existsById(eventId);
        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    void deleteEvent_NullId() {
        assertThrows(IllegalArgumentException.class, () -> eventService.deleteEvent(null));

        verify(eventRepository, never()).existsById(any());
        verify(eventRepository, never()).deleteById(any());
    }
}