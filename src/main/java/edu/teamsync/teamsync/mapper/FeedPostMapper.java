package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.feedPostDTO.*;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FeedPostMapper {

    // Map entity to response DTO
    @Mapping(target = "authorId", source = "author.id")
    FeedPostResponseDTO toResponse(FeedPosts feedPost);

    // Map entity to detail DTO with reactions
    @Mapping(target = "authorId", source = "author.id")
    FeedPostWithReactionDTO toDetailDto(FeedPosts feedPost);

    // Map create request to entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.ZonedDateTime.now())")
    @Mapping(target = "isAiGenerated", constant = "false")
    @Mapping(target = "aiSummary", ignore = true)
    FeedPosts toEntity(FeedPostCreateRequest request);

    // Update entity from update request
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(FeedPostUpdateRequest request, @MappingTarget FeedPosts feedPost);

    // Map list of entities to list of responses
    List<FeedPostResponseDTO> toResponseList(List<FeedPosts> feedPosts);

    // Custom method to set reactions manually
    default FeedPostWithReactionDTO toDetailDtoWithReactions(FeedPosts feedPost, List<ReactionDetailDTO> reactions) {
        FeedPostWithReactionDTO dto = toDetailDto(feedPost);
        dto.setReactions(reactions);
        return dto;
    }
}