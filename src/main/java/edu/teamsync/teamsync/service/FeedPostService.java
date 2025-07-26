package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.feedPostDTO.*;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionDetailDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.Reactions;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.FeedPostMapper;
import edu.teamsync.teamsync.mapper.ReactionMapper;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.ReactionRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.ZonedDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class FeedPostService {

    private final FeedPostRepository feedPostsRepository;
    private final UserRepository usersRepository;
    private final FeedPostMapper feedPostMapper;
    private final ReactionMapper reactionMapper;
    private final ReactionRepository reactionsRepository;

    public List<FeedPostResponseDTO> getAllFeedPosts() {
        List<FeedPosts> feedPosts = feedPostsRepository.findAll();
        return feedPostMapper.toResponseList(feedPosts);
    }

    public void createFeedPost(FeedPostCreateRequest request,String userEmail) {
        Users currentUser = usersRepository.findByEmail(userEmail);
        if (currentUser == null) {
            throw new NotFoundException("User not found with email "+userEmail);
        }

        FeedPosts feedPost = feedPostMapper.toEntity(request);
        feedPost.setAuthor(currentUser); // Set author manually

        feedPostsRepository.save(feedPost);
//        return feedPostMapper.toResponse(savedPost);
    }

    public FeedPostWithReactionDTO getFeedPostById(Long id) {
        FeedPosts feedPost = feedPostsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FeedPost not found with id: " + id));

        List<Reactions> reactions = reactionsRepository.findByPostId(feedPost.getId());
        List<ReactionDetailDTO> reactionsDetailsDTO=reactionMapper.reactionsToDTO(reactions);

        return feedPostMapper.toDetailDtoWithReactions(feedPost, reactionsDetailsDTO);
    }

    public void updateFeedPost(Long id, FeedPostUpdateRequest request) {
        FeedPosts existingPost = feedPostsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("FeedPost not found with id: " + id));

        // Update basic fields using mapper
        feedPostMapper.updateEntityFromRequest(request, existingPost);
        feedPostsRepository.save(existingPost);
//        return feedPostMapper.toResponse(updatedPost);
    }

    public void deleteFeedPost(Long id) {
        if (!feedPostsRepository.existsById(id)) {
            throw new NotFoundException("FeedPost not found with id: " + id);
        }
        // Delete associated reactions first
        reactionsRepository.deleteByPostId(id);
        // Delete the feed post
        feedPostsRepository.deleteById(id);
    }
}