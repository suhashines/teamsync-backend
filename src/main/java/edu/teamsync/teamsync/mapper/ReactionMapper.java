package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.entity.Reactions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReactionMapper {
    @Mapping(source = "user.id", target = "userId")
    ReactionResponseDTO reactionResponseToDTO(Reactions reaction);

    List<ReactionResponseDTO> reactionsResponseToDTO(List<Reactions> reactions);
    @Mapping(source = "user.id", target = "userId")
    List<ReactionDetailDTO> reactionsToDTO(List<Reactions> reactions);
}
