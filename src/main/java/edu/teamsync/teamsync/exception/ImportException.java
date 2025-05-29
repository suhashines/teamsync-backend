package edu.teamsync.teamsync.exception;

import java.util.Map;

public class ImportException extends RuntimeException {
    private final String scope;

    public ImportException(String scope, String message) {
        super(message);
        this.scope = scope;
    }

    public Map<String, String> toMap() {
        return Map.of(scope, getMessage());
    }
}
