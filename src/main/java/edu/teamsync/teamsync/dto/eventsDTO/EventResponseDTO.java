package edu.teamsync.teamsync.dto.eventsDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.teamsync.teamsync.entity.Events.EventType;
import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EventResponseDTO(
        Long id,
        String title,
        String description,
        EventType type,
        LocalDate date,
        List<Long> participantIds,
        LocalDate tentativeStartingDate
) {}
