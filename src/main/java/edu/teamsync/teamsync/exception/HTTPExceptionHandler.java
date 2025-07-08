package edu.teamsync.teamsync.exception;

import edu.teamsync.teamsync.dto.ErrorMessageDto;
import edu.teamsync.teamsync.exception.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HTTPExceptionHandler {
    @Value("${debug:false}")
    boolean debug;

    Logger logger = LoggerFactory.getLogger(HTTPExceptionHandler.class);

    /**
     * Handle not found error.
     * This method maps NotFoundException to HTTP status code 404
     */

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ErrorMessage handleNotFound(Exception e) {

        logger.error("NOT FOUND", e);
        return new ErrorMessage("NOT_FOUND", e.getMessage());
    }

     /**
     * Handle security exception.
     * This method maps SecurityException to HTTP status code 403
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(SecurityException.class)
    public ErrorMessageDto handleSecurityException(SecurityException e) {
        logger.error("FORBIDDEN", e);
        return new ErrorMessageDto(e.getMessage(), "FORBIDDEN");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidDataAccessResourceUsageException.class)
    public ErrorMessageDto handleUnauthorizedException(InvalidDataAccessResourceUsageException e) {
        logger.error("UNAUTHORIZED", e);
        return new ErrorMessageDto("Invalid authorization headers", "UNAUTHORIZED");
    }
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ErrorMessage handleAuthorizationDeniedException(org.springframework.security.authorization.AuthorizationDeniedException e) {
        logger.error("AUTHORIZATION_DENIED", e);
        return new ErrorMessage("Access Denied", "Only project admins or owners can perform this action");
    }
}
