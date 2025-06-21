package edu.teamsync.teamsync.mapper;
import edu.teamsync.teamsync.dto.projectDTO.ProjectDTO;
import edu.teamsync.teamsync.dto.projectDTO.ProjectMemberDTO;
import edu.teamsync.teamsync.entity.ProjectMembers;
import edu.teamsync.teamsync.entity.Projects;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(source = "createdBy.id", target = "createdBy")
    ProjectDTO toDto(Projects project);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "role", target = "role", qualifiedByName = "roleToString")
    ProjectMemberDTO toMemberDto(ProjectMembers member);

    @Named("roleToString")
    default String roleToString(ProjectMembers.ProjectRole role) {
        return role != null ? role.name() : null;
    }

    @Named("stringToRole")
    default ProjectMembers.ProjectRole stringToRole(String role) {
        return role != null ? ProjectMembers.ProjectRole.valueOf(role) : null;
    }
}
