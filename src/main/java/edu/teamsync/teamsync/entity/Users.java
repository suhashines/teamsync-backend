package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email; //findByEmail(String email)

    @Column(nullable = false)
    private String password;

    private String profilePicture;

    private String designation; //prithu namer manager ke ke -> findByName, findByDesignation , findByNameAndDesignation(String name,String designation)

    private LocalDate birthdate;

    private LocalDate joinDate; //01-07-2025 (minDate), 31-07-2025(maxDate) , findByJoinDateBetween(LocalDate minDate,LocalDate maxDate)

    private Boolean predictedBurnoutRisk; //findByPredictedBurnoutRiskTrue()
}