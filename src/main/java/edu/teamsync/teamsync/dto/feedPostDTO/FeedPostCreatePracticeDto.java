package edu.teamsync.teamsync.dto.feedPostDTO;

import java.time.LocalDate;

import edu.teamsync.teamsync.entity.FeedPosts.FeedPostType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedPostCreatePracticeDto {
    @NotNull(message="must provide type")
     private FeedPostType type;
     @NotNull(message="content should be here")
     private String content;
      private String[] mediaUrls;
       private LocalDate eventDate;
       private String[] pollOptions;
}
