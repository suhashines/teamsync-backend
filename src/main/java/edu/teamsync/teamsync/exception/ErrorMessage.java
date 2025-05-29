package edu.teamsync.teamsync.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * The type Error message.
 * This class is used to send the error message response.
 * The response contains the error code and the error message.
 * 
 * @attribute code (String): Error code
 * @attribute message (String): Error message
 * @attribute details (Map<String, String>)
 * @attribute errors (List<String>)
 * @attribute status (HttpStatus)
 */
@Schema(name = "ErrorMessage", description = "Error message response")
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ErrorMessage {
    @Schema(example = "INVALID_REQUEST_PARAMETER", description = "Error code")
    private final String code;

    @Schema(example = "Invalid email or password", description = "Error message")
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> details;

    @Schema()
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    @Schema(hidden = true)
    @JsonIgnore
    private HttpStatus status;
}
