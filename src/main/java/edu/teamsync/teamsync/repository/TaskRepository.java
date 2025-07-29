package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.entity.Users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Long> {

    @Query("SELECT t FROM Tasks t LEFT JOIN FETCH t.project LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.assignedBy LEFT JOIN FETCH t.parentTask WHERE t.id = :id")
    Optional<Tasks> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM Tasks t WHERE t.parentTask.id = :parentTaskId")
    List<Tasks> findSubtasksByParentTaskId(@Param("parentTaskId") Long parentTaskId);

    // New method for getting tasks by project ID
    @Query("SELECT t FROM Tasks t LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.assignedBy LEFT JOIN FETCH t.parentTask WHERE t.project.id = :projectId")
    List<Tasks> findByProjectIdWithDetails(@Param("projectId") Long projectId);

    // Method for getting tasks by project ID and status (useful for kanban)
    @Query("SELECT t FROM Tasks t LEFT JOIN FETCH t.assignedTo LEFT JOIN FETCH t.assignedBy LEFT JOIN FETCH t.parentTask WHERE t.project.id = :projectId AND t.status = :status")
    List<Tasks> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") Tasks.TaskStatus status);

    @Query("SELECT t FROM Tasks t WHERE t.assignedBy.id = :userId OR t.assignedTo.id = :userId ORDER BY t.assignedAt DESC")
    List<Tasks> findUserInvolvedTasks(@Param("userId") Long userId);

    @Query("SELECT t FROM Tasks t WHERE t.assignedTo.id = :userId ORDER BY t.assignedAt DESC")
    List<Tasks> findTasksAssignedToUser(@Param("userId") Long userId);
}