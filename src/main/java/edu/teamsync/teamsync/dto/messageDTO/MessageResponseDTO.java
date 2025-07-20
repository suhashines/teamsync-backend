package edu.teamsync.teamsync.dto.messageDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.ZonedDateTime;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageResponseDTO(
        Long id,
        Long senderId,
        Long channelId,
        Long recipientId,
        String content,
        String fileUrl,
        String fileType,
        ZonedDateTime timestamp,
        Long threadParentId
) {}