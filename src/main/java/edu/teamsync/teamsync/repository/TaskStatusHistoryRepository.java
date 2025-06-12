package edu.teamsync.teamsync.repository;



import edu.teamsync.teamsync.entity.TaskStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskStatusHistoryRepository extends JpaRepository<TaskStatusHistory, Long> {

    @Query("SELECT tsh FROM TaskStatusHistory tsh LEFT JOIN FETCH tsh.changedBy WHERE tsh.task.id = :taskId ORDER BY tsh.changedAt DESC")
    List<TaskStatusHistory> findByTaskIdOrderByChangedAtDesc(@Param("taskId") Long taskId);
}