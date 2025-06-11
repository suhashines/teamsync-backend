package edu.teamsync.teamsync.dto.feedPostDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class FeedPostUpdateRequest {
    @NotNull(message = "Type is required")
    private FeedPosts.FeedPostType type;
    @NotNull(message = "Author Id is required")
    private Long authorId;
    private String content;
    private String[] mediaUrls;
    private ZonedDateTime createdAt;
    private LocalDate eventDate;
    private String[] pollOptions;
    private Boolean isAiGenerated;
    private String aiSummary;
    private List<ReactionDetailDTO> reactions;
}
