package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.dto.commentDTO.*;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.entity.Comments;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.Reactions;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.CommentMapper;
import edu.teamsync.teamsync.mapper.ReactionMapper;
import edu.teamsync.teamsync.repository.CommentReactionRepository;
import edu.teamsync.teamsync.repository.CommentRepository;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private FeedPostRepository feedPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ReactionMapper reactionMapper;

    @Mock
    private CommentReactionRepository commentReactionRepository;

    @InjectMocks
    private CommentService commentService;

    private final Long postId = 1L;
    private final Long commentId = 1L;
    private final String userEmail = "test@example.com";
    private final Long userId = 1L;

    private FeedPosts post;
    private Users user;
    private Comments comment;
    private Comments parentComment;
    private CommentCreateRequestDTO createRequestDTO;
    private CommentUpdateRequestDTO updateRequestDTO;
    private ReplyCreateRequestDTO replyRequestDTO;
    private ReactionCreateRequestDTO reactionRequestDTO;
    private CommentResponseDTO commentResponseDTO;
    private List<ReactionResponseDTO> reactionResponseDTOList;

    @BeforeEach
    void setUp() {
        post = FeedPosts.builder().id(postId).build();
        user = Users.builder().id(userId).email(userEmail).build();
        parentComment = Comments.builder().id(2L).post(post).replyCount(0).build();
        comment = Comments.builder()
                .id(commentId)
                .post(post)
                .author(user)
                .content("Test comment")
                .timestamp(ZonedDateTime.now())
                .parentComment(parentComment)
                .replyCount(0)
                .build();

        createRequestDTO = new CommentCreateRequestDTO();
        createRequestDTO.setPostId(postId);
        createRequestDTO.setContent("New comment");
        createRequestDTO.setParentCommentId(parentComment.getId());

        updateRequestDTO = new CommentUpdateRequestDTO();
        updateRequestDTO.setPostId(postId);
        updateRequestDTO.setAuthorId(userId);
        updateRequestDTO.setContent("Updated comment");
        updateRequestDTO.setParentCommentId(parentComment.getId());
        ReactionDetailDTO reactionDetailDTO = new ReactionDetailDTO();
        reactionDetailDTO.setUserId(userId);
        reactionDetailDTO.setReactionType("like");
        updateRequestDTO.setReactions(Collections.singletonList(reactionDetailDTO));

        replyRequestDTO = new ReplyCreateRequestDTO();
        replyRequestDTO.setContent("Reply content");
        replyRequestDTO.setAuthor_id(userId);

        reactionRequestDTO = new ReactionCreateRequestDTO();
        reactionRequestDTO.setUserId(userId);
        reactionRequestDTO.setReactionType("like");

        commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(commentId);
        commentResponseDTO.setPostId(postId);
        commentResponseDTO.setAuthorId(userId);
        commentResponseDTO.setContent("Test comment");
        commentResponseDTO.setTimestamp(comment.getTimestamp());
        commentResponseDTO.setParentCommentId(parentComment.getId());
        commentResponseDTO.setReplyCount(0);

        reactionResponseDTOList = Collections.singletonList(new ReactionResponseDTO());
    }

    @Test
    void getAllCommentsByPostId_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdOrderByTimestamp(postId)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.toResponseDTOList(anyList())).thenReturn(Collections.singletonList(commentResponseDTO));

        List<CommentResponseDTO> result = commentService.getAllCommentsByPostId(postId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentId, result.getFirst().getId());
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdOrderByTimestamp(postId);
        verify(commentMapper).toResponseDTOList(anyList());
    }

    @Test
    void getAllCommentsByPostId_NotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getAllCommentsByPostId(postId));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void getCommentById_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toResponseDTO(comment)).thenReturn(commentResponseDTO);

        CommentResponseDTO result = commentService.getCommentById(postId, commentId);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(commentMapper).toResponseDTO(comment);
    }

    @Test
    void getCommentById_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getCommentById(postId, commentId));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void getCommentById_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getCommentById(postId, commentId));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void createComment_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findByEmail(userEmail)).thenReturn(user);
        when(commentRepository.findById(createRequestDTO.getParentCommentId())).thenReturn(Optional.of(parentComment));
        when(commentMapper.toEntity(createRequestDTO)).thenReturn(comment);
        when(commentRepository.save(any(Comments.class))).thenReturn(comment);

        commentService.createComment(postId, createRequestDTO, userEmail);

        verify(feedPostRepository).findById(postId);
        verify(userRepository).findByEmail(userEmail);
        verify(commentMapper).toEntity(createRequestDTO);
        verify(commentRepository, times(2)).save(any(Comments.class)); //  2 saves (comment and parentComment)
    }


    @Test
    void createComment_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.createComment(postId, createRequestDTO, userEmail));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void createComment_UserNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findByEmail(userEmail)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> commentService.createComment(postId, createRequestDTO, userEmail));
        verify(feedPostRepository).findById(postId);
        verify(userRepository).findByEmail(userEmail);
    }

    @Test
    void updateComment_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentMapper).updateEntityFromDTO(updateRequestDTO,comment);
        when(commentRepository.save(any(Comments.class))).thenReturn(comment);
        when(userRepository.findById(reactionRequestDTO.getUserId())).thenReturn(Optional.of(user));

        commentService.updateComment(postId, commentId, updateRequestDTO);

        verify(feedPostRepository).findById(postId);
        verify(commentRepository,times(1)).findByPostIdAndCommentId(postId, commentId);
        verify(commentMapper).updateEntityFromDTO(updateRequestDTO, comment);
        verify(commentRepository).save(any(Comments.class));
    }


    @Test
    void updateComment_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.updateComment(postId, commentId, updateRequestDTO));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void updateComment_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.updateComment(postId, commentId, updateRequestDTO));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void deleteComment_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(parentComment)).thenReturn(parentComment);

        commentService.deleteComment(postId, commentId);

        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(commentReactionRepository).deleteByCommentId(commentId);
        verify(commentRepository).save(parentComment);
        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.deleteComment(postId, commentId));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void deleteComment_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.deleteComment(postId, commentId));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void getAllReactionsByCommentId_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(commentReactionRepository.findByCommentId(commentId)).thenReturn(Collections.singletonList(new Reactions()));
        when(reactionMapper.reactionsResponseToDTO(anyList())).thenReturn(reactionResponseDTOList);

        List<ReactionResponseDTO> result = commentService.getAllReactionsByCommentId(postId, commentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(commentReactionRepository).findByCommentId(commentId);
        verify(reactionMapper).reactionsResponseToDTO(anyList());
    }

    @Test
    void getAllReactionsByCommentId_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getAllReactionsByCommentId(postId, commentId));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void getAllReactionsByCommentId_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getAllReactionsByCommentId(postId, commentId));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void addReactionToComment_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentReactionRepository.findByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like))
                .thenReturn(Optional.empty());
        when(commentReactionRepository.save(any(Reactions.class))).thenReturn(new Reactions());

        commentService.addReactionToComment(postId, commentId, reactionRequestDTO);

        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
        verify(commentReactionRepository).findByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like);
        verify(commentReactionRepository).save(any(Reactions.class));
    }

    @Test
    void addReactionToComment_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addReactionToComment(postId, commentId, reactionRequestDTO));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void addReactionToComment_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addReactionToComment(postId, commentId, reactionRequestDTO));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void addReactionToComment_UserNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addReactionToComment(postId, commentId, reactionRequestDTO));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
    }

    @Test
    void removeReactionFromComment_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentReactionRepository.findByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like))
                .thenReturn(Optional.of(new Reactions()));

        commentService.removeReactionFromComment(postId, commentId, userId, "like");

        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
        verify(commentReactionRepository).findByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like);
        verify(commentReactionRepository).deleteByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like);
    }

    @Test
    void removeReactionFromComment_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.removeReactionFromComment(postId, commentId, userId, "like"));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void removeReactionFromComment_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.removeReactionFromComment(postId, commentId, userId, "like"));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void removeReactionFromComment_UserNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.removeReactionFromComment(postId, commentId, userId, "like"));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
    }

    @Test
    void removeReactionFromComment_ReactionNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentReactionRepository.findByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.removeReactionFromComment(postId, commentId, userId, "like"));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
        verify(commentReactionRepository).findByUserIdAndCommentIdAndReactionType(userId, commentId, Reactions.ReactionType.like);
    }

    @Test
    void getAllRepliesByCommentId_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.findRepliesByPostIdAndParentCommentId(postId, commentId)).thenReturn(Collections.singletonList(comment));
        when(commentMapper.toResponseDTOList(anyList())).thenReturn(Collections.singletonList(commentResponseDTO));

        List<CommentResponseDTO> result = commentService.getAllRepliesByCommentId(postId, commentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentId, result.getFirst().getId());
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(commentRepository).findRepliesByPostIdAndParentCommentId(postId, commentId);
        verify(commentMapper).toResponseDTOList(anyList());
    }

    @Test
    void getAllRepliesByCommentId_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getAllRepliesByCommentId(postId, commentId));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void getAllRepliesByCommentId_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.getAllRepliesByCommentId(postId, commentId));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void addReplyToComment_Success() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comments.class))).thenReturn(comment);

        commentService.addReplyToComment(postId, commentId, replyRequestDTO);

        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
        verify(commentRepository, times(2)).save(any(Comments.class));
    }

    @Test
    void addReplyToComment_PostNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addReplyToComment(postId, commentId, replyRequestDTO));
        verify(feedPostRepository).findById(postId);
    }

    @Test
    void addReplyToComment_CommentNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addReplyToComment(postId, commentId, replyRequestDTO));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
    }

    @Test
    void addReplyToComment_UserNotFound() {
        when(feedPostRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findByPostIdAndCommentId(postId, commentId)).thenReturn(Optional.of(comment));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.addReplyToComment(postId, commentId, replyRequestDTO));
        verify(feedPostRepository).findById(postId);
        verify(commentRepository).findByPostIdAndCommentId(postId, commentId);
        verify(userRepository).findById(userId);
    }
}