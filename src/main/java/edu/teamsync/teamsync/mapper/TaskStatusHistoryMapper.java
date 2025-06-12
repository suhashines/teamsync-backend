package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.taskDTO.TaskStatusHistoryDTO;
import edu.teamsync.teamsync.entity.TaskStatusHistory;
import edu.teamsync.teamsync.entity.Tasks;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskStatusHistoryMapper {

    @Mapping(source = "status", target = "status", qualifiedByName = "taskStatusToString")
    @Mapping(source = "changedBy.id", target = "changedBy")
    TaskStatusHistoryDTO toDto(TaskStatusHistory entity);

    List<TaskStatusHistoryDTO> toDtoList(List<TaskStatusHistory> entities);

    @Named("taskStatusToString")
    default String taskStatusToString(Tasks.TaskStatus status) {
        return status != null ? status.name() : null;
    }
}
