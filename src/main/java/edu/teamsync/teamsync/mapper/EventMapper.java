package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.eventsDTO.EventCreationDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventResponseDTO;
import edu.teamsync.teamsync.dto.eventsDTO.EventUpdateDTO;
import edu.teamsync.teamsync.dto.userDTO.UserUpdateDTO;
import edu.teamsync.teamsync.entity.Events;
import edu.teamsync.teamsync.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "participants", source = "participantIds")
    @Mapping(target = "parentPost", ignore = true)
    @Mapping(target = "tentativeStartingDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Events toEntity(EventCreationDTO dto);

    @Mapping(source = "participants", target = "participantIds")
    EventResponseDTO toDto(Events entity);

    @Mapping(target = "parentPost", ignore = true)
    @Mapping(target = "tentativeStartingDate", ignore = true)
    @Mapping(target = "id", ignore = true)
    Events toEntity(EventUpdateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentPost", ignore = true)
    @Mapping(target = "tentativeStartingDate", ignore = true)
    void updateEventFromDTO(EventUpdateDTO eventUpdateDTO, @MappingTarget Events event);
}