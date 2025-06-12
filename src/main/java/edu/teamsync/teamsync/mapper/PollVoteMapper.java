package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteResponseDTO;
import edu.teamsync.teamsync.entity.PollVotes;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PollVoteMapper {

    @Mapping(source = "poll.id", target = "pollId")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "selectedOption", target = "selectedOption")
    PollVoteResponseDTO toDTO(PollVotes pollVote);
}