package edu.teamsync.teamsync.mapper;
import edu.teamsync.teamsync.dto.messageDTO.MessageCreationDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageResponseDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageUpdateDTO;
import edu.teamsync.teamsync.entity.Messages;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "channel", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "threadParent", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    Messages toEntity(MessageCreationDTO dto);

    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "channel.id", target = "channelId")
    @Mapping(source = "recipient.id", target = "recipientId")
    @Mapping(source = "threadParent.id", target = "threadParentId")
//    @Mapping(target = "sentimentScore", constant = "0.0")
//    @Mapping(target = "suggestedReplies", expression = "java(java.util.Arrays.asList())")
    MessageResponseDTO toDto(Messages entity);

    @Mapping(target = "sender", ignore = true)
    @Mapping(target = "channel", ignore = true)
    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "threadParent", ignore = true)
    @Mapping(target = "id", ignore = true)
    Messages toEntity(MessageUpdateDTO dto);
}
