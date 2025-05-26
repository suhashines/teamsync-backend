package edu.teamsync.teamsync.dto;

import edu.teamsync.teamsync.entity.Projects;

public class ProjectDTO {
    private Long id;
    private String title;
    private UserDTO createdBy;

    public ProjectDTO(Projects project) {
        this.id = project.getId();
        this.title = project.getTitle();
        this.createdBy = (project.getCreatedBy() != null)
                ? new UserDTO(project.getCreatedBy().getId(), project.getCreatedBy().getName(), project.getCreatedBy().getEmail())
                : null;
    }
}