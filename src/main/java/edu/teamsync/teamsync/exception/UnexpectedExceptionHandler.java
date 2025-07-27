package edu.teamsync.teamsync.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Map;

/**
 * The type Global Controller Exception Handler. This class maps exceptions to HTTP status codes
 */
@ControllerAdvice
@ResponseBody
public class UnexpectedExceptionHandler {
    @Value("${debug:false}")
    boolean debug;

    Logger logger = LoggerFactory.getLogger(UnexpectedExceptionHandler.class);


    /**
     * Handle internal server error.
     * This method maps all other exceptions that are not matched
     * with any other specific exception to HTTP status code 500
     * @param e the exception
     * @return the error message
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ErrorMessage handleInternalServerError(Exception e) {

        logger.error("INTERNAL SERVER ERROR", e);
        return ErrorMessage.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("Something went wrong. Please try again later")
                .errors(debug ? List.of(e.getMessage()) : null)
                .details(Map.of(e.getClass().getName(), e.getMessage()))
                .build();
    }
}
