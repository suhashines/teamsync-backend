package edu.teamsync.teamsync.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostCreatePracticeDto;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.response.SuccessResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeedPostPracticeService {

    private final UserService userService ;
    private final FeedPostRepository repository;

    public SuccessResponse<Object> createFeedPost(FeedPostCreatePracticeDto dto){
        //fetch user
        Users user = userService.getCurrentUser();
        // dto -> FeedPost 
        FeedPosts post = FeedPosts.builder().content(dto.getContent()).eventDate(dto.getEventDate()).mediaUrls(dto.getMediaUrls()).pollOptions(dto.getPollOptions()).type(dto.getType()).author(user).build();

        FeedPosts savedPost = repository.save(post);

        return SuccessResponse.builder().code(HttpStatus.CREATED.value()).status(HttpStatus.CREATED).data(savedPost).build();
    }
}
