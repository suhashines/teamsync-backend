package edu.teamsync.teamsync.dto.messageDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.ZonedDateTime;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageResponseDTO(
        Long id,
        Long senderId,
        Long channelId,
        Long recipientId,
        String content,
        ZonedDateTime timestamp,
        Long threadParentId
) {}