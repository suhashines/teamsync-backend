//package edu.teamsync.teamsync.dto.channelDTO;
//
//
//import edu.teamsync.teamsync.entity.Channels.ChannelType;
//import java.util.List;
//
//public record ChannelResponseDTO(
//        Long id,
//        String name,
//        ChannelType type,
//        Long projectId,
//        List<Long> memberIds
//) {}

package edu.teamsync.teamsync.dto.channelDTO;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import edu.teamsync.teamsync.entity.Channels.ChannelType;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ChannelResponseDTO(
        Long id,
        String name,
        ChannelType type,
        Long projectId,
        List<Long> members
) {}