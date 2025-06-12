package edu.teamsync.teamsync.dto.pollVoteDTO;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PollVoteResponseDTO {
    private Long id;
    private Long pollId;
    private Long userId;
    private String selectedOption;
}