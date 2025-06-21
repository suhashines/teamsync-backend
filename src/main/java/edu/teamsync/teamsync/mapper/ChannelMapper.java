package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.channelDTO.ChannelRequestDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelResponseDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelUpdateDTO;
import edu.teamsync.teamsync.entity.Channels;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ChannelMapper {
    // create new
    @Mapping(source = "projectId", target = "project.id")
    @Mapping(source = "memberIds", target = "members")
    Channels toEntity(ChannelRequestDTO dto);

    // read
    @Mapping(source = "project.id", target = "projectId")
    ChannelResponseDTO toDto(Channels entity);

    // update existing
    @Mapping(target = "id", ignore = true)                // never overwrite the PK
    void updateEntityFromDto(ChannelUpdateDTO dto, @MappingTarget Channels entity);
}
