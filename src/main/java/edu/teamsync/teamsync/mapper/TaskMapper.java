package edu.teamsync.teamsync.mapper;
import edu.teamsync.teamsync.dto.taskDTO.TaskCreationDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskResponseDTO;
import edu.teamsync.teamsync.dto.taskDTO.TaskUpdateDTO;
import edu.teamsync.teamsync.entity.Tasks;
import edu.teamsync.teamsync.entity.TaskStatusHistory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "assignedBy", ignore = true)
    @Mapping(target = "parentTask", ignore = true)
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
    @Mapping(source = "priority", target = "priority", qualifiedByName = "stringToTaskPriority")
    Tasks toEntity(TaskCreationDTO dto);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignedTo.id", target = "assignedTo")
    @Mapping(source = "assignedBy.id", target = "assignedBy")
    @Mapping(source = "parentTask.id", target = "parentTaskId")
    @Mapping(source = "status", target = "status", qualifiedByName = "taskStatusToString")
    @Mapping(source = "priority", target = "priority", qualifiedByName = "taskPriorityToString")
    @Mapping(source = "aiPriority", target = "aiPriority", qualifiedByName = "taskPriorityToString")
    @Mapping(target = "subtasks", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    TaskResponseDTO toDto(Tasks entity);


    @Mapping(target = "assignedAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "assignedBy", ignore = true)
    @Mapping(target = "parentTask", ignore = true)
    @Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
    @Mapping(source = "priority", target = "priority", qualifiedByName = "stringToTaskPriority")
    @Mapping(source = "aiPriority", target = "aiPriority", qualifiedByName = "stringToTaskPriority")
    void updateEntityFromDto(TaskUpdateDTO dto, @MappingTarget Tasks entity);

    @Named("stringToTaskStatus")
    default Tasks.TaskStatus stringToTaskStatus(String status) {
        return status != null ? Tasks.TaskStatus.valueOf(status) : null;
    }

    @Named("taskStatusToString")
    default String taskStatusToString(Tasks.TaskStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToTaskPriority")
    default Tasks.TaskPriority stringToTaskPriority(String priority) {
        return priority != null ? Tasks.TaskPriority.valueOf(priority) : null;
    }

    @Named("taskPriorityToString")
    default String taskPriorityToString(Tasks.TaskPriority priority) {
        return priority != null ? priority.name() : null;
    }
}