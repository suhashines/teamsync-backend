package edu.teamsync.teamsync.dto.authDTO;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;
    private String profile_picture;
    @NotBlank(message = "Designation is required")
    private String designation;
}