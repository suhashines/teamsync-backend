package edu.teamsync.teamsync.exception;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
@ResponseBody
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidationExceptionHandler {

    @Value("${debug:false}")
    boolean debug;

    /**
     * Handle bad request.
     * <p>
     * This method maps IllegalArgumentException to HTTP status code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            PropertyReferenceException.class
    })
    public ErrorMessage handleBadRequest(Exception e) {
        return ErrorMessage.builder()
                .code("INVALID_REQUEST_PARAMETER")
                .message(e.getMessage())
                .errors(debug ? List.of(e.getMessage()) : null)
                .build();
    }

    /**
     * Handle missing request parameter.
     * This method maps MissingServletRequestParameterException to HTTP status code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ErrorMessage handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        Map<String, String> details = new HashMap<>();
        details.put("parameter", e.getParameterName());
        details.put("type", e.getParameterType());

        return ErrorMessage.builder()
                .code("MISSING_REQUEST_PARAMETER")
                .message("Required request parameter '" + e.getParameterName() + "' is missing")
                .errors(debug ? List.of(e.getMessage()) : null)
                .details(details)
                .build();
    }

    /**
     * Handle method argument not valid.
     * This method maps MethodArgumentNotValidException to HTTP status code 400
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorMessage handleMethodArgumentNotValid(BindException e) {
        List<String> messages = new ArrayList<>();
        Map<String, String> details = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            messages.add(fieldError.getDefaultMessage());
            if (details.containsKey(fieldError.getField())) {
                details.put(fieldError.getField(), details.get(fieldError.getField()) + ", " + fieldError.getDefaultMessage());
            } else {
                details.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
        });

        e.getBindingResult().getGlobalErrors().forEach(globalError -> {
            messages.add(globalError.getDefaultMessage());
        });

        return ErrorMessage.builder()
                .code("INVALID_REQUEST_BODY_FIELD")
                .message("Invalid request body field(s)")
                .errors(messages)
                .details(details)
                .build();
    }

    /**
     * Handle invalid body type exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ErrorMessage handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return ErrorMessage.builder()
                .code("INVALID_BODY_TYPE")
                .message("Invalid body type, only application/json is supported")
                .errors(debug ? List.of(e.getMessage()) : null)
                .build();
    }

    /**
     * Handle invalid body type exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorMessage handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ErrorMessage.builder()
                .code("INVALID_BODY")
                .message("Invalid body, please provide a valid JSON body")
                .errors(debug ? List.of(e.getMessage()) : null)
                .build();
    }

    /**
     * Handle invalid request type exception.
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ErrorMessage handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return ErrorMessage.builder()
                .code("INVALID_REQUEST_METHOD")
                .message("Invalid request method, please use appropriate method")
                .errors(debug ? List.of(e.getMessage()) : null)
                .build();
    }
}