package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "taskstatushistory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Tasks task;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Tasks.TaskStatus status;


    @ManyToOne
    @JoinColumn(name = "changed_by", nullable = false)
    private Users changedBy;

    @Builder.Default
    @Column(name = "changed_at", nullable = false)
    private ZonedDateTime changedAt = ZonedDateTime.now();

    private String comment;
}