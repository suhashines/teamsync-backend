package edu.teamsync.teamsync.exception.http;

/**
 * This class is used to handle not found exception
 * HTTP Status Code: 404
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
