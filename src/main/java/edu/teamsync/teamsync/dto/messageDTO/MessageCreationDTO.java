package edu.teamsync.teamsync.dto.messageDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageCreationDTO(
        @NotBlank(message = "Content cannot be blank")
        String content,
        Long recipientId,
        Long channelId,
        Long threadParentId
) {}
