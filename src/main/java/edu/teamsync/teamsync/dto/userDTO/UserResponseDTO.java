package edu.teamsync.teamsync.dto.userDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String profilePicture;
    private String designation;
    private LocalDate birthdate;
    private LocalDate joinDate;
    private Boolean predictedBurnoutRisk;
}