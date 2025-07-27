package edu.teamsync.teamsync.authorization;

import edu.teamsync.teamsync.entity.ProjectMembers;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.exception.http.UnauthorizedException;
import edu.teamsync.teamsync.repository.ProjectMemberRepository;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.repository.TaskRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectAuthorizationService {

    private final ProjectRepository projectsRepository;
    private final ProjectMemberRepository projectMembersRepository;
    private final UserRepository usersRepository;
    private final TaskRepository taskRepository;

    /**
     * Check if the current user is admin or owner of the project
     */
    public boolean isProjectAdminOrOwner(Long projectId) {
        String userEmail = getCurrentUserEmail();
        return hasAdminOrOwnerRole(projectId, userEmail);
    }

    public boolean canManageTask(Long taskId) {
        // Get the task's project ID directly from repository
        Optional<Tasks> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new NotFoundException("Task not found with id: " + taskId);
        }

        Tasks task = taskOpt.get();
        Long projectId = task.getProject().getId(); // Assuming task has project relationship

        return isProjectAdminOrOwner(projectId);
    }

    /**
     * Check if the current user is admin or owner of the project, throw exception if not
     */
    public void requireProjectAdminOrOwner(Long projectId) {
        if (!isProjectAdminOrOwner(projectId)) {
            throw new UnauthorizedException("Only project admins or owners can perform this action");
        }
    }

    /**
     * Check if user has admin or owner role in the project
     */
    public boolean hasAdminOrOwnerRole(Long projectId, String userEmail) {
        // Find user by email
        Users user = usersRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Find project
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Project not found"));

        // Check if user is project creator (owner)
        if (project.getCreatedBy().getId().equals(user.getId())) {
            return true;
        }

        // Check if user is project member with admin or owner role
        Optional<ProjectMembers> membership = projectMembersRepository
                .findByProjectIdAndUserId(projectId, user.getId());

        if (membership.isPresent()) {
            ProjectMembers.ProjectRole role = membership.get().getRole();
            return role == ProjectMembers.ProjectRole.admin ||
                    role == ProjectMembers.ProjectRole.owner;
        }

        return false;
    }

    /**
     * Get current authenticated user's email
     */
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User not authenticated");
        }
        return authentication.getName();
    }
}
