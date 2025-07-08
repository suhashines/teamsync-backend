package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.projectDTO.*;
import edu.teamsync.teamsync.entity.ProjectMembers;
import edu.teamsync.teamsync.entity.Projects;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.ProjectMapper;
import edu.teamsync.teamsync.repository.ProjectMemberRepository;
import edu.teamsync.teamsync.repository.ProjectRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMemberRepository projectMemberRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private final Long projectId = 1L;
    private final Long userId = 1L;
    private final Long creatorId = 2L;
    private final String userEmail = "test@example.com";
    private final String creatorEmail = "creator@example.com";

    private Projects project;
    private Users user;
    private Users creator;
    private ProjectMembers projectMember;
    private ProjectCreationDTO projectCreationDTO;
    private ProjectUpdateDTO projectUpdateDTO;
    private ProjectDTO projectDTO;
    private AddMemberDTO addMemberDTO;
    private UpdateMemberRoleDTO updateMemberRoleDTO;
    private ProjectMemberDTO projectMemberDTO;
    private InitialMemberDTO initialMemberDTO;

    @BeforeEach
    void setUp() {
        creator = Users.builder()
                .id(creatorId)
                .email(creatorEmail)
                .build();

        user = Users.builder()
                .id(userId)
                .email(userEmail)
                .build();

        project = Projects.builder()
                .id(projectId)
                .title("Test Project")
                .description("Test Description")
                .createdBy(creator)
                .createdAt(ZonedDateTime.now())
                .build();

        projectMember = ProjectMembers.builder()
                .project(project)
                .user(user)
                .role(ProjectMembers.ProjectRole.member)
                .joinedAt(ZonedDateTime.now())
                .build();

        initialMemberDTO = InitialMemberDTO.builder()
                .userId(userId)
                .role("member")
                .build();

        projectCreationDTO = ProjectCreationDTO.builder()
                .title("New Project")
                .description("New Description")
                .initialMembers(Collections.singletonList(initialMemberDTO))
                .build();

        projectMemberDTO = ProjectMemberDTO.builder()
                .userId(userId)
                .role("member")
                .joinedAt(ZonedDateTime.now())
                .build();

        projectUpdateDTO = ProjectUpdateDTO.builder()
                .title("Updated Project")
                .description("Updated Description")
                .createdBy(creatorId)
                .createdAt(ZonedDateTime.now())
                .members(Collections.singletonList(projectMemberDTO))
                .build();

        projectDTO = ProjectDTO.builder()
                .id(projectId)
                .title("Test Project")
                .description("Test Description")
                .createdBy(creatorId)
                .createdAt(ZonedDateTime.now())
                .members(Collections.singletonList(projectMemberDTO))
                .build();

        addMemberDTO = AddMemberDTO.builder()
                .userId(userId)
                .role("member")
                .build();

        updateMemberRoleDTO = UpdateMemberRoleDTO.builder()
                .role("admin")
                .build();
    }

    @Test
    void getAllProjects_Success() {
        when(projectRepository.findAll()).thenReturn(Collections.singletonList(project));
        when(projectMapper.toDto(project)).thenReturn(projectDTO);

        List<ProjectDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(projectId, result.get(0).getId());
        verify(projectRepository).findAll();
        verify(projectMapper).toDto(project);
    }

    @Test
    void getProjectById_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectDTO);

        ProjectDTO result = projectService.getProjectById(projectId);

        assertNotNull(result);
        assertEquals(projectId, result.getId());
        verify(projectRepository).findById(projectId);
        verify(projectMapper).toDto(project);
    }

    @Test
    void getProjectById_NotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.getProjectById(projectId));
        verify(projectRepository).findById(projectId);
    }

    @Test
    void createProject_Success() {
        when(userRepository.findByEmail(creatorEmail)).thenReturn(creator);
        when(projectRepository.save(any(Projects.class))).thenReturn(project);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectMemberRepository.saveAll(anyList())).thenReturn(Collections.singletonList(projectMember));

        projectService.createProject(projectCreationDTO, creatorEmail);

        verify(userRepository).findByEmail(creatorEmail);
        verify(projectRepository).save(any(Projects.class));
        verify(userRepository).findById(userId);
        verify(projectMemberRepository).saveAll(anyList());
    }

    @Test
    void createProject_CreatorNotFound() {
        when(userRepository.findByEmail(creatorEmail)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> projectService.createProject(projectCreationDTO, creatorEmail));
        verify(userRepository).findByEmail(creatorEmail);
    }

    @Test
    void createProject_InitialMemberNotFound() {
        when(userRepository.findByEmail(creatorEmail)).thenReturn(creator);
        when(projectRepository.save(any(Projects.class))).thenReturn(project);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.createProject(projectCreationDTO, creatorEmail));
        verify(userRepository).findByEmail(creatorEmail);
        verify(projectRepository).save(any(Projects.class));
        verify(userRepository).findById(userId);
    }

    @Test
    void createProject_WithoutInitialMembers_Success() {
        ProjectCreationDTO dtoWithoutMembers = ProjectCreationDTO.builder()
                .title("New Project")
                .description("New Description")
                .initialMembers(null)
                .build();

        when(userRepository.findByEmail(creatorEmail)).thenReturn(creator);
        when(projectRepository.save(any(Projects.class))).thenReturn(project);

        projectService.createProject(dtoWithoutMembers, creatorEmail);

        verify(userRepository).findByEmail(creatorEmail);
        verify(projectRepository).save(any(Projects.class));
        verify(projectMemberRepository, never()).saveAll(anyList());
    }

    @Test
    void updateProject_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectMemberRepository.saveAll(anyList())).thenReturn(Collections.singletonList(projectMember));
        when(projectRepository.save(any(Projects.class))).thenReturn(project);

        projectService.updateProject(projectId, projectUpdateDTO);

        verify(projectRepository).findById(projectId);
        verify(projectMemberRepository).deleteByProjectId(projectId);
        verify(userRepository).findById(userId);
        verify(projectMemberRepository).saveAll(anyList());
        verify(projectRepository).save(any(Projects.class));
    }

    @Test
    void updateProject_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.updateProject(projectId, projectUpdateDTO));
        verify(projectRepository).findById(projectId);
    }

    @Test
    void updateProject_MemberNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.updateProject(projectId, projectUpdateDTO));
        verify(projectRepository).findById(projectId);
        verify(projectMemberRepository).deleteByProjectId(projectId);
        verify(userRepository).findById(userId);
    }

    @Test
    void updateProject_WithoutMembers_Success() {
        ProjectUpdateDTO dtoWithoutMembers = ProjectUpdateDTO.builder()
                .title("Updated Project")
                .description("Updated Description")
                .members(null)
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Projects.class))).thenReturn(project);

        projectService.updateProject(projectId, dtoWithoutMembers);

        verify(projectRepository).findById(projectId);
        verify(projectMemberRepository, never()).deleteByProjectId(projectId);
        verify(projectRepository).save(any(Projects.class));
    }

    @Test
    void deleteProject_Success() {
        when(projectRepository.existsById(projectId)).thenReturn(true);

        projectService.deleteProject(projectId);

        verify(projectRepository).existsById(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    void deleteProject_NotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> projectService.deleteProject(projectId));
        verify(projectRepository).existsById(projectId);
    }

    @Test
    void deleteProject_NullId() {
        assertThrows(IllegalArgumentException.class, () -> projectService.deleteProject(null));
    }

    @Test
    void getProjectMembers_Success() {
        when(projectRepository.existsById(projectId)).thenReturn(true);
        when(projectMemberRepository.findByProjectId(projectId)).thenReturn(Collections.singletonList(projectMember));
        when(projectMapper.toMemberDto(projectMember)).thenReturn(projectMemberDTO);

        List<ProjectMemberDTO> result = projectService.getProjectMembers(projectId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).getUserId());
        verify(projectRepository).existsById(projectId);
        verify(projectMemberRepository).findByProjectId(projectId);
        verify(projectMapper).toMemberDto(projectMember);
    }

    @Test
    void getProjectMembers_ProjectNotFound() {
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> projectService.getProjectMembers(projectId));
        verify(projectRepository).existsById(projectId);
    }

    @Test
    void addMemberToProject_Success() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);
        when(projectMemberRepository.save(any(ProjectMembers.class))).thenReturn(projectMember);

        projectService.addMemberToProject(projectId, addMemberDTO);

        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectMemberRepository).existsByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository).save(any(ProjectMembers.class));
    }

    @Test
    void addMemberToProject_ProjectNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.addMemberToProject(projectId, addMemberDTO));
        verify(projectRepository).findById(projectId);
    }

    @Test
    void addMemberToProject_UserNotFound() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.addMemberToProject(projectId, addMemberDTO));
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
    }

    @Test
    void addMemberToProject_UserAlreadyMember() {
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> projectService.addMemberToProject(projectId, addMemberDTO));
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectMemberRepository).existsByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void addMemberToProject_InvalidRole() {
        AddMemberDTO invalidRoleDTO = AddMemberDTO.builder()
                .userId(userId)
                .role("INVALID_ROLE")
                .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(projectMemberRepository.existsByProjectIdAndUserId(projectId, userId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> projectService.addMemberToProject(projectId, invalidRoleDTO));
        verify(projectRepository).findById(projectId);
        verify(userRepository).findById(userId);
        verify(projectMemberRepository).existsByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void updateMemberRole_Success() {
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(projectMember));
        when(projectMemberRepository.save(any(ProjectMembers.class))).thenReturn(projectMember);

        projectService.updateMemberRole(projectId, userId, updateMemberRoleDTO);

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository).save(any(ProjectMembers.class));
    }

    @Test
    void updateMemberRole_MemberNotFound() {
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.updateMemberRole(projectId, userId, updateMemberRoleDTO));
        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void updateMemberRole_InvalidRole() {
        UpdateMemberRoleDTO invalidRoleDTO = UpdateMemberRoleDTO.builder()
                .role("INVALID_ROLE")
                .build();

        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(projectMember));

        assertThrows(IllegalArgumentException.class, () -> projectService.updateMemberRole(projectId, userId, invalidRoleDTO));
        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }

    @Test
    void removeMemberFromProject_Success() {
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.of(projectMember));

        projectService.removeMemberFromProject(projectId, userId);

        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
        verify(projectMemberRepository).delete(projectMember);
    }

    @Test
    void removeMemberFromProject_MemberNotFound() {
        when(projectMemberRepository.findByProjectIdAndUserId(projectId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> projectService.removeMemberFromProject(projectId, userId));
        verify(projectMemberRepository).findByProjectIdAndUserId(projectId, userId);
    }
}