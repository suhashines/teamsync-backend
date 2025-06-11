package edu.teamsync.teamsync.dto.eventsDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.teamsync.teamsync.entity.Events.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record EventUpdateDTO(

        @NotBlank(message = "Title cannot be blank")
        String title,

        String description,

        @NotNull(message = "Type cannot be null")
        EventType type,

        @NotNull(message = "Date cannot be null")
        LocalDate date,

        @NotNull(message = "Participants cannot be null")
        @Size(min = 1, message = "At least one participant is required")
        List<Long> participants
) {}
