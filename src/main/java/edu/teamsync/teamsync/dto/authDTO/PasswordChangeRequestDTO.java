package edu.teamsync.teamsync.dto.authDTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordChangeRequestDTO {
    @NotBlank(message = "Password is required")
    private String currentPassword;
    @NotBlank(message = "New Password is required")
    private String newPassword;
}
