# Exception Handling Documentation

This document outlines the exception handling mechanism in the Spring Boot project, detailing each exception, its purpose, the HTTP status code it maps to, and example usage in a service class.

## Exception Handlers

### 1. DBExceptionHandler
Handles database-related exceptions, primarily `DataIntegrityViolationException` and optimistic locking exceptions (`OptimisticLockException`, `ObjectOptimisticLockingFailureException`).

- **Exceptions Handled**:
  - `DataIntegrityViolationException` (HTTP 400 or 409):
    - **Unique Constraint Violation** (HTTP 409): Triggered when a duplicate entry violates a unique constraint. Error code: `DUPLICATE_ENTRY`.
    - **Check Constraint Violation** (HTTP 400): Triggered when a value violates a check constraint. Error code: `INVALID_VALUE`.
    - **Foreign Key Constraint Violation** (HTTP 409): Triggered when a foreign key constraint is violated. Error code: `FOREIGN_KEY_CONSTRAINT`.
    - **Null Constraint Violation** (HTTP 400): Triggered when a required field is missing. Error code: `REQUIRED_FIELD_MISSING`.
    - **Generic Data Integrity Violation** (HTTP 409): Fallback for other data integrity issues. Error code: `DATA_INTEGRITY_VIOLATION`.
  - `OptimisticLockException`, `ObjectOptimisticLockingFailureException` (HTTP 409): Triggered when concurrent data modifications occur. Error code: `DATA_MODIFIED`.

- **Example Response**:
  ```json
  {
    "code": "DUPLICATE_ENTRY",
    "message": "Duplicate entry for username",
    "errors": ["duplicate key value violates unique constraint 'username_key'"],
    "status": 409
  }
  ```

### 2. HTTPExceptionHandler
Handles HTTP-related exceptions, focusing on resource access and authorization issues.

- **Exceptions Handled**:
  - `NotFoundException` (HTTP 404): Triggered when a requested resource is not found. Error code: `NOT_FOUND`.
  - `SecurityException` (HTTP 403): Triggered for forbidden access. Error code: `FORBIDDEN`.
  - `InvalidDataAccessResourceUsageException` (HTTP 401): Currently mapped to unauthorized access with a message about invalid authorization headers. Error code: `UNAUTHORIZED`. **Note**: This mapping may be incorrect; consider replacing with `UnauthorizedException`.

- **Example Response**:
  ```json
  {
    "code": "NOT_FOUND",
    "message": "User with ID 123 not found"
  }
  ```

### 3. ValidationExceptionHandler
Handles validation-related exceptions for request parameters and body.

- **Exceptions Handled**:
  - `IllegalArgumentException`, `ConstraintViolationException`, `PropertyReferenceException` (HTTP 400): Invalid request parameters. Error code: `INVALID_REQUEST_PARAMETER`.
  - `BindException` (HTTP 400): Invalid request body fields (e.g., validation failures in `@Valid` DTOs). Error code: `INVALID_REQUEST_BODY_FIELD`.
  - `HttpMediaTypeNotSupportedException` (HTTP 400): Invalid content type (e.g., non-JSON body). Error code: `INVALID_BODY_TYPE`.
  - `HttpMessageNotReadableException` (HTTP 400): Malformed JSON body. Error code: `INVALID_BODY`.
  - `HttpRequestMethodNotSupportedException` (HTTP 400): Incorrect HTTP method. Error code: `INVALID_REQUEST_METHOD`.

- **Example Response**:
  ```json
  {
    "code": "INVALID_REQUEST_BODY_FIELD",
    "message": "Invalid request body field(s)",
    "errors": ["Email must be valid", "Password cannot be blank"],
    "details": {
      "email": "Email must be valid",
      "password": "Password cannot be blank"
    }
  }
  ```

### 4. UnexpectedExceptionHandler
Catches all unhandled exceptions as a fallback.

- **Exceptions Handled**:
  - `Exception` (HTTP 500): Any unhandled exception. Error code: `INTERNAL_SERVER_ERROR`.

- **Example Response**:
  ```json
  {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "Something went wrong. Please try again later",
    "errors": ["NullPointerException at com.example.SomeClass"],
    "details": {
      "java.lang.NullPointerException": "null"
    }
  }
  ```

### 5. Custom Exceptions
- **NotFoundException** (HTTP 404): Thrown when a resource is not found. Handled by `HTTPExceptionHandler`.
- **UnauthorizedException** (HTTP 401): Defined but not handled. Falls back to `UnexpectedExceptionHandler` (HTTP 500), which is undesirable. **Recommendation**: Add handling in `HTTPExceptionHandler`.
- **ImportException**: Thrown for import-related errors with a scope and message. Not handled, falls back to HTTP 500. **Recommendation**: Add handling in a dedicated handler.
- **UserException**: Generic user-related exception. Not handled, falls back to HTTP 500. **Recommendation**: Add handling with an appropriate status (e.g., 400).

## Example Usage in a Service Class

Below is an example of a service class that interacts with a repository and throws exceptions, focusing on ID-based operations and leveraging the existing exception handling.

```java
package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.model.User;
import edu.teamsync.teamsync.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
    }

    public User createUser(User user) {
        // Validation is assumed to be handled by @Valid in the controller
        // DataIntegrityViolationException (e.g., duplicate username) is handled by DBExceptionHandler
        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with ID " + id + " not found"));
        
        // Update fields
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        
        // Optimistic locking or data integrity violations are handled by DBExceptionHandler
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User with ID " + id + " not found");
        }
        // Foreign key constraints are handled by DBExceptionHandler
        userRepository.deleteById(id);
    }
}
```

### Do Service Classes Need to Handle Exceptions Other Than `NotFoundException` for ID-Based Operations?

For ID-based operations (e.g., `findById`, `deleteById`), service classes typically only need to handle `NotFoundException` explicitly, as shown in the example above. The reasons are:

- **Database Exceptions**: The `DBExceptionHandler` automatically handles `DataIntegrityViolationException` (e.g., duplicate entries, foreign key violations) and optimistic locking exceptions, so the service layer does not need to catch these. For example, attempting to save a user with a duplicate username will trigger a `DUPLICATE_ENTRY` response (HTTP 409) without service-level handling.

- **Validation Exceptions**: Input validation (e.g., invalid email format) is typically handled at the controller layer using `@Valid` on DTOs, with `ValidationExceptionHandler` catching `BindException` or `ConstraintViolationException`. The service layer can assume validated input.

- **Generic Exceptions**: Any unexpected errors (e.g., `NullPointerException`) are caught by `UnexpectedExceptionHandler` and returned as HTTP 500, so the service layer does not need to handle these explicitly.

- **Other Custom Exceptions**:
  - `UnauthorizedException`: If authentication is required (e.g., checking user permissions), the service might throw `UnauthorizedException`. However, since it’s not currently handled, it results in a 500 error. **Recommendation**: Handle `UnauthorizedException` in `HTTPExceptionHandler` for proper 401 responses.
  - `UserException` and `ImportException`: If the service layer throws these, they will be caught by `UnexpectedExceptionHandler` (HTTP 500). **Recommendation**: Add specific handlers for these exceptions to return meaningful status codes (e.g., 400 for `UserException`, 400 for `ImportException`).

**Conclusion**: For ID-based operations, the service layer only needs to throw `NotFoundException` when a resource is not found (e.g., via `findById().orElseThrow()`). Other exceptions like `DataIntegrityViolationException`, validation errors, or unexpected errors are handled globally by the respective `@ControllerAdvice` classes, reducing the burden on the service layer. However, if you use `UserException` or `ImportException` in your service layer, you should add dedicated handlers to avoid 500 errors.

## Recommendations for Service Classes
- **Throw `NotFoundException` for Missing Resources**: Always throw `NotFoundException` when an ID-based lookup fails, as shown in the example.
- **Avoid Catching Global Exceptions**: Let `DBExceptionHandler`, `ValidationExceptionHandler`, and `UnexpectedExceptionHandler` handle database, validation, and unexpected errors, respectively.
- **Handle Custom Exceptions Explicitly**: If using `UserException` or `ImportException`, ensure they are handled by a dedicated `@ControllerAdvice` to return appropriate status codes.
- **Add Handler for `UnauthorizedException`**: Update `HTTPExceptionHandler` to handle `UnauthorizedException` for proper 401 responses.
- **Validate Inputs in Controller**: Use `@Valid` on DTOs to catch validation errors early, relying on `ValidationExceptionHandler`.