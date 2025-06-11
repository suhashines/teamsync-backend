//package edu.teamsync.teamsync.mapper;
//
//import edu.teamsync.teamsync.dto.commentDTO.CommentCreateRequestDTO;
//import edu.teamsync.teamsync.dto.commentDTO.CommentResponseDTO;
//import edu.teamsync.teamsync.dto.commentDTO.CommentUpdateRequestDTO;
//import edu.teamsync.teamsync.entity.Comments;
//import org.mapstruct.*;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring")
//public interface CommentMapper {
//
//    @Mapping(source = "post.id", target = "postId")
//    @Mapping(source = "author.id", target = "authorId")
//    @Mapping(source = "parentComment.id", target = "parentCommentId")
//    @Mapping(target = "reactions", ignore = true) // Handle reactions separately
//    CommentResponseDTO toResponseDTO(Comments comment);
//
//    List<CommentResponseDTO> toResponseDTOList(List<Comments> comments);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "post", ignore = true)
//    @Mapping(target = "author", ignore = true)
//    @Mapping(target = "parentComment", ignore = true)
//    @Mapping(target = "timestamp", ignore = true)
//    @Mapping(target = "replyCount", ignore = true)
//    Comments toEntity(CommentCreateRequestDTO dto);
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "post", ignore = true)
//    @Mapping(target = "author", ignore = true)
//    @Mapping(target = "parentComment", ignore = true)
//    Comments toEntity(CommentUpdateRequestDTO dto);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "post", ignore = true)
//    @Mapping(target = "author", ignore = true)
//    @Mapping(target = "parentComment", ignore = true)
//    void updateEntityFromDTO(CommentUpdateRequestDTO dto, @MappingTarget Comments comment);
//}

package edu.teamsync.teamsync.mapper;

import edu.teamsync.teamsync.dto.commentDTO.CommentCreateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentResponseDTO;
import edu.teamsync.teamsync.dto.commentDTO.CommentUpdateRequestDTO;
import edu.teamsync.teamsync.dto.commentDTO.ReplyCreateRequestDTO;
import edu.teamsync.teamsync.entity.Comments;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    @Mapping(source = "author.id", target = "authorId")
    @Mapping(source = "parentComment.id", target = "parentCommentId")
    @Mapping(target = "reactions", ignore = true) // Handle reactions separately
    CommentResponseDTO toResponseDTO(Comments comment);

    List<CommentResponseDTO> toResponseDTOList(List<Comments> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    Comments toEntity(CommentCreateRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    Comments toEntity(CommentUpdateRequestDTO dto);

    // NEW MAPPING FOR REPLY DTO TO ENTITY
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "replyCount", ignore = true)
    @Mapping(source = "author_id", target = "author.id")
    Comments toEntity(ReplyCreateRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    void updateEntityFromDTO(CommentUpdateRequestDTO dto, @MappingTarget Comments comment);
}