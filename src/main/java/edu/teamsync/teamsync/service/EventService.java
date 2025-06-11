
package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.eventsDTO.EventCreationDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventResponseDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventUpdateDTO;
import edu.teamsync.teamsync.entity.Events;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.EventMapper;
import edu.teamsync.teamsync.repository.EventRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventMapper eventMapper;

    @Transactional
    public void createEvent(EventCreationDTO requestDto) {
        // Validate participant existence - throw NotFoundException for missing users
        List<Long> participantIds = requestDto.participantIds();
        for (Long participantId : participantIds) {
            if (!userRepository.existsById(participantId)) {
                throw new NotFoundException("User with ID " + participantId + " not found");
            }
        }

        Events event = eventMapper.toEntity(requestDto);

        // DataIntegrityViolationException (e.g., duplicate constraints) will be handled by DBExceptionHandler
        eventRepository.save(event);
//        return eventMapper.toDto(savedEvent);
    }

    public EventResponseDTO getEventById(Long id) {
        // Validate that ID is not null
        if (id == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }

        Events event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with ID " + id + " not found"));

        EventResponseDTO eventDto = eventMapper.toDto(event);
        return eventDto;
    }

    public List<EventResponseDTO> getAllEvents() {
        List<Events> events = eventRepository.findAll();
        return events.stream()
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateEvent(Long id, EventUpdateDTO requestDto) {
        // Validate input parameters
        if (id == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }

        if (requestDto == null) {
            throw new IllegalArgumentException("Update request cannot be null");
        }

        // Check if event exists - throw NotFoundException if not found
        Events event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with ID " + id + " not found"));

        // Validate participant existence - throw NotFoundException for missing users
        List<Long> participantIds = requestDto.participants();
        if (participantIds != null) {
            for (Long participantId : participantIds) {
                if (!userRepository.existsById(participantId)) {
                    throw new NotFoundException("User with ID " + participantId + " not found");
                }
            }
        }
        eventMapper.updateEventFromDTO(requestDto,event);
        eventRepository.save(event);
//        return eventMapper.toDto(savedEvent);
    }

    @Transactional
    public void deleteEvent(Long id) {
        // Validate that ID is not null
        if (id == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }

        // Check if event exists - throw NotFoundException if not found
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Event with ID " + id + " not found");
        }

        // Delete the event - Foreign key constraint violations will be handled by DBExceptionHandler
        eventRepository.deleteById(id);
    }
}