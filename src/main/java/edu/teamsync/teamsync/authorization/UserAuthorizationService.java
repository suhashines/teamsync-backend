package edu.teamsync.teamsync.authorization;

import edu.teamsync.teamsync.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthorizationService {

    private final UserService userService;

    /**
     * Check if the current user has manager designation
     */
    public boolean isManager() {
        return userService.isCurrentUserManager();
    }

    /**
     * Check if the current user has manager designation, throw exception if not
     */
    public void requireManagerRole() {
        if (!isManager()) {
            throw new edu.teamsync.teamsync.exception.http.UnauthorizedException(
                "Only users with manager designation can perform this action"
            );
        }
    }
} 