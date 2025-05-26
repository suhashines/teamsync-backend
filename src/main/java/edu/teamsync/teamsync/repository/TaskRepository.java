package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Tasks, Long> {

    // Find tasks assigned to a specific user
    List<Tasks> findByAssignedToId(Long userId);

    // Find tasks within projects the user is a member of
    @Query("SELECT t FROM Tasks t WHERE t.project.id IN (SELECT pm.project.id FROM ProjectMembers pm WHERE pm.user.id = :userId)")
    List<Tasks> findByUserProjects(Long userId);
}