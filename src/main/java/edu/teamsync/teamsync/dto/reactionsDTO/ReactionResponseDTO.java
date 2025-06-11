package edu.teamsync.teamsync.dto.reactionsDTO;

import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReactionResponseDTO {
    private Long id;
    private Long userId;
    private String reactionType;
    private ZonedDateTime createdAt;
}
