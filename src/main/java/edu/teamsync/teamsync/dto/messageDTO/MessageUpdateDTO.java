package edu.teamsync.teamsync.dto.messageDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageUpdateDTO(
        @NotNull(message = "Sender ID cannot be null")
        Long senderId,
        @NotNull(message = "Channel id cannot be null")
        Long channelId,
        @NotNull(message = "Recipient id cannot be null")
        Long recipientId,

        @NotBlank(message = "Content cannot be blank")
        String content,
        ZonedDateTime timestamp,
        Long threadParentId
) {}