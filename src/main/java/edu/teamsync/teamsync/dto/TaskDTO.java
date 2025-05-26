package edu.teamsync.teamsync.dto;

import edu.teamsync.teamsync.entity.Tasks;

public class TaskDTO {
    private Long id;
    private String title;
    private String status;
    private UserDTO assignedTo;
    private ProjectDTO project;

    // Constructor, Getters, and Setters
    public TaskDTO(Tasks task) {
        this.id = task.getId();
        this.title = task.getTitle();
//        this.status = task.getStatus();
        this.assignedTo = (task.getAssignedTo() != null)
                ? new UserDTO(task.getAssignedTo().getId(), task.getAssignedTo().getName(), task.getAssignedTo().getEmail())
                : null;
        this.project = (task.getProject() != null)
                ? new ProjectDTO(task.getProject())
                : null;
    }
}