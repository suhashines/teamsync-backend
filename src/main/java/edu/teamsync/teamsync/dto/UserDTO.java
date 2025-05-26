package edu.teamsync.teamsync.dto;


public class UserDTO {
    private Long id;
    private String name;
    private String email;

    // Constructor, Getters, and Setters
    public UserDTO(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}

