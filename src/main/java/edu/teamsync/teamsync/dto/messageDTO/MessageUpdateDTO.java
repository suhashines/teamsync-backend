package edu.teamsync.teamsync.dto.messageDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class MessageUpdateDTO {
        private  String content;
}