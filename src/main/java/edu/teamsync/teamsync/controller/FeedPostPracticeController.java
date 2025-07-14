package edu.teamsync.teamsync.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.teamsync.teamsync.dto.feedPostDTO.FeedPostCreatePracticeDto;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.FeedPostPracticeService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/practice/feedposts")
@RequiredArgsConstructor
public class FeedPostPracticeController {

    
    private final FeedPostPracticeService service ;

    @PostMapping
    public SuccessResponse<Object> createFeedPost(@RequestBody FeedPostCreatePracticeDto dto) {
        //TODO: process POST request
        
        return service.createFeedPost(dto);
    }
    
}
