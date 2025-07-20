package edu.teamsync.teamsync.dto.messageDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class MessageUpdateDTO {
        private final Long channelId;
        private final Long recipientId;
        private final String content;

        public MessageUpdateDTO(Long channelId, Long recipientId,
                                @NotBlank(message = "Content cannot be blank") String content) {
                if (channelId == null && recipientId == null) {
                        throw new IllegalArgumentException("Either channelId or recipientId must be provided");
                }
                if (channelId != null && recipientId != null) {
                        throw new IllegalArgumentException("Only one of channelId or recipientId should be provided, not both");
                }

                this.channelId = channelId;
                this.recipientId = recipientId;
                this.content = content;
        }
}