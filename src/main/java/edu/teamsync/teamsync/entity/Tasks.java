package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tasks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    private ZonedDateTime deadline;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private String timeEstimate;

    private String aiTimeEstimate;

    @Enumerated(EnumType.STRING)
    private TaskPriority aiPriority;

    private ZonedDateTime smartDeadline;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Projects project;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private Users assignedTo;

    @ManyToOne
    @JoinColumn(name = "assigned_by")
    private Users assignedBy;

    @Column(name = "assigned_at")
    private ZonedDateTime assignedAt;

    @ManyToOne
    @JoinColumn(name = "parent_task_id")
    private Tasks parentTask;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "attachments", columnDefinition = "text[]")
    private List<String> attachments;

    @Column(name = "tentative_starting_date")
    private LocalDate tentativeStartingDate;

    public enum TaskStatus {
        backlog, todo, in_progress, in_review, blocked, completed
    }

    public enum TaskPriority {
        low, medium, high, urgent
    }
}
