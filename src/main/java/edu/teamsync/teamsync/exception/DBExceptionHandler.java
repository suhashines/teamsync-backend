package edu.teamsync.teamsync.exception;

import jakarta.persistence.OptimisticLockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DBExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(DBExceptionHandler.class);

    @Value("${debug:false}")
    boolean debug;

    public static final Pattern UNIQUE_KEY = Pattern.compile("[a-zA-Z0-9_]+_key");
    public static final Pattern CHECK_CONSTRAINT = Pattern.compile("[a-zA-Z0-9_]+_check");
    public static final Pattern FOREIGN_KEY = Pattern.compile("[a-zA-Z0-9_]+_fkey");

    public ErrorMessage convertMessage(DataIntegrityViolationException e) {
        logger.error("DataIntegrityViolationException: {}", e.getMessage());
        if (e.getMessage().contains("unique constraint")) {
            Matcher matched = UNIQUE_KEY.matcher(e.getMessage());

            return ErrorMessage.builder()
                    .code("DUPLICATE_ENTRY")
                    .message(
                        matched.find() ?
                        "Duplicate entry for " + matched.group().replace("_key", "") :
                        "Duplicate entry for resource"
                    )
                    .errors(debug ? List.of(e.getMessage()) : null)
                    .status(HttpStatus.CONFLICT)
                    .build();

        } else if (e.getMessage().contains("check constraint")) {
            Matcher matched = CHECK_CONSTRAINT.matcher(e.getMessage());

            return ErrorMessage.builder()
                    .code("INVALID_VALUE")
                    .message(
                        matched.find() ?
                        "Invalid value for " + matched.group().replace("_check", "") :
                        "Invalid value for resource"
                    )
                    .errors(debug ? List.of(e.getMessage()) : null)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else if (e.getMessage().contains("foreign key")) {
            Matcher matched = FOREIGN_KEY.matcher(e.getMessage());
            return ErrorMessage.builder()
                    .code("FOREIGN_KEY_CONSTRAINT")
                    .message(
                        matched.find() ?
                        "Foreign key constraint violation for " + matched.group().replace("_fkey", "") :
                        "Foreign key constraint violation"
                    )
                    .errors(debug ? List.of(e.getMessage()) : null)
                    .status(HttpStatus.CONFLICT)
                    .build();
        } else if (e.getMessage().contains("null")) {
            return ErrorMessage.builder()
                    .code("REQUIRED_FIELD_MISSING")
                    .message("Required field is missing")
                    .errors(debug ? List.of(e.getMessage()) : null)
                    .status(HttpStatus.BAD_REQUEST)
                    .build();
        } else {
            return ErrorMessage.builder()
                    .code("DATA_INTEGRITY_VIOLATION")
                    .message("Data integrity violation")
                    .errors(debug ? List.of(e.getMessage()) : null)
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    /**
     * Handle conflict.
     * This method maps DataIntegrityViolationException to HTTP status code 409 / 400
     * 
     * @param e DataIntegrityViolationException
     * @return ErrorMessage {@link ErrorMessage}
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorMessage> handleConflict(DataIntegrityViolationException e) {
        ErrorMessage msg = convertMessage(e);
        return new ResponseEntity<>(msg, msg.getStatus());
    }

    /**
     * Handle conflict.
     * This method maps OptimisticLockException to HTTP status code 409
     * @param e OptimisticLockException
     * @return ErrorMessage {@link ErrorMessage}
     */
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({OptimisticLockException.class, ObjectOptimisticLockingFailureException.class})
    public ErrorMessage handleConflict(Exception e) {
        return ErrorMessage.builder()
                .code("DATA_MODIFIED")
                .message("Data has been modified by another user. Please refresh and try again")
                .errors(debug ? List.of(e.getMessage()) : null)
                .status(HttpStatus.CONFLICT)
                .build();
    }
}
