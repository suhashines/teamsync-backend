package edu.teamsync.teamsync.dto.messageDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record MessageUpdateDTO(
        @NotNull(message = "Channel id cannot be null")
        Long channelId,
        @NotNull(message = "Recipient id cannot be null")
        Long recipientId,

        @NotBlank(message = "Content cannot be blank")
        String content

) {}