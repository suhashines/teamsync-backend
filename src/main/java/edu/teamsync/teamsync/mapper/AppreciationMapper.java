package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationCreateDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationResponseDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationUpdateDTO;
import edu.teamsync.teamsync.entity.Appreciations;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AppreciationMapper {
    AppreciationMapper INSTANCE = Mappers.getMapper(AppreciationMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parentPost", ignore = true)
    @Mapping(target = "fromUser", ignore = true)
    @Mapping(target = "toUser", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(source = "message", target = "message")
    Appreciations toEntity(AppreciationCreateDTO dto);

    @Mapping(source = "parentPost.id", target = "parentPostId")
    @Mapping(source = "fromUser.id", target = "fromUserId")
    @Mapping(source = "fromUser.name", target = "fromUserName")
    @Mapping(source = "toUser.id", target = "toUserId")
    @Mapping(source = "toUser.name", target = "toUserName")
    AppreciationResponseDTO toResponseDTO(Appreciations entity);

    @Mapping(target = "parentPost", ignore = true)
    @Mapping(target = "fromUser", ignore = true)
    @Mapping(target = "toUser", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(AppreciationUpdateDTO dto, @MappingTarget Appreciations entity);
}
