//package edu.teamsync.teamsync.repository;
//
//import edu.teamsync.teamsync.entity.ProjectMemberId;
//import edu.teamsync.teamsync.entity.ProjectMembers;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//// For @IdClass approach
//@Repository
//public interface ProjectMemberRepository extends JpaRepository<ProjectMembers, ProjectMemberId> {
//
//    @Modifying
//    @Query("DELETE FROM ProjectMembers pm WHERE pm.project.id = :projectId")
//    void deleteByProjectId(@Param("projectId") Long projectId);
//
//    // Optional: Additional useful methods
//    List<ProjectMembers> findByProjectId(Long projectId);
//    List<ProjectMembers> findByUserId(Long userId);
//
//    @Query("SELECT pm FROM ProjectMembers pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
//    Optional<ProjectMembers> findByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);
//}
//
//// If you need to find by composite key
//// ProjectMembersId id = new ProjectMembersId(projectId, userId);
//// Optional<ProjectMembers> member = projectMemberRepository.findById(id);
package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.ProjectMemberId;
import edu.teamsync.teamsync.entity.ProjectMembers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// For @IdClass approach
@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMembers, ProjectMemberId> {

    @Modifying
    @Query("DELETE FROM ProjectMembers pm WHERE pm.project.id = :projectId")
    void deleteByProjectId(@Param("projectId") Long projectId);

    // Find methods
    List<ProjectMembers> findByProjectId(Long projectId);
    List<ProjectMembers> findByUserId(Long userId);

    @Query("SELECT pm FROM ProjectMembers pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    Optional<ProjectMembers> findByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    // Existence check method - ADD THIS METHOD
    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    // Alternative using @Query if the above doesn't work
    @Query("SELECT COUNT(pm) > 0 FROM ProjectMembers pm WHERE pm.project.id = :projectId AND pm.user.id = :userId")
    boolean existsByProjectIdAndUserIdCustom(@Param("projectId") Long projectId, @Param("userId") Long userId);
}

// If you need to find by composite key
// ProjectMembersId id = new ProjectMembersId(projectId, userId);
// Optional<ProjectMembers> member = projectMemberRepository.findById(id);