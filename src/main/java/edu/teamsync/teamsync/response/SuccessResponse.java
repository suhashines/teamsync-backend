package edu.teamsync.teamsync.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * The type Success response.
 * This class is used to send the success response.
 * The response contains the success code, message, and optional data.
 *
 * @attribute code (String): Success code
 * @attribute message (String): Success message
 * @attribute data (Object): Response data
 * @attribute metadata (Map): Additional metadata
 * @attribute status (HttpStatus): HTTP status
 */
@Schema(name = "SuccessResponse", description = "Success response message")
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class SuccessResponse<T> {

    @Schema(example = "200", description = "Success code")
    private final int code;
    @Schema(example = "OK", description = "HTTP status")
    private HttpStatus status;
    @Schema(example = "Operation completed successfully", description = "Success message")
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Response data")
    private T data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Additional metadata")
    private Map<String, Object> metadata;

}
