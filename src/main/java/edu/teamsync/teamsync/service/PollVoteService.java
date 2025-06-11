
package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteCreationDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteResponseDTO;
import edu.teamsync.teamsync.dto.pollVoteDTO.PollVoteUpdateDTO;
import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.PollVotes;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.PollVoteMapper;
import edu.teamsync.teamsync.repository.FeedPostRepository;
import edu.teamsync.teamsync.repository.PollVoteRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class PollVoteService {
    @Autowired
    private PollVoteRepository pollVotesRepository;
    @Autowired
    private FeedPostRepository feedPostsRepository;
    @Autowired
    private UserRepository usersRepository;
    @Autowired
    private PollVoteMapper pollVotesMapper;

    public List<PollVoteResponseDTO> getAllPollVotes() {
        return pollVotesRepository.findAll().stream()
                .map(pollVotesMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PollVoteResponseDTO getPollVoteById(Long id) {
        PollVotes pollVote = pollVotesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Poll vote not found with id: " + id));

        return  pollVotesMapper.toDTO(pollVote);

    }

    public void createPollVote(PollVoteCreationDTO request, String userEmail) {
        Users user = usersRepository.findByEmail(userEmail);
        if (user == null) {
            throw new NotFoundException("User not found with email "+userEmail);
        }

        FeedPosts poll = feedPostsRepository.findById(request.getPollId())
                .orElseThrow(() -> new NotFoundException("Poll not found with id: " + request.getPollId()));

        // Validate that the post is actually a poll
        if (poll.getType() != FeedPosts.FeedPostType.poll) {
            throw new NotFoundException("The specified post is not a poll");
        }

        if (!isValidPollOption(poll.getPollOptions(), request.getSelectedOption())) {
            throw new NotFoundException("Invalid poll option selected "+ request.getSelectedOption());
        }
        PollVotes pollVote = PollVotes.builder()
                .poll(poll)
                .user(user)
                .selectedOption(request.getSelectedOption())
                .build();

        pollVotesRepository.save(pollVote);
    }

    public void updatePollVote(Long id, PollVoteUpdateDTO request) {
        PollVotes existingPollVote = pollVotesRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Poll vote not found with id: " + id));

        FeedPosts poll = feedPostsRepository.findById(request.getPollId())
                .orElseThrow(() -> new NotFoundException("Poll not found with id: " + request.getPollId()));

        Users user = usersRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getUserId()));

        if (!isValidPollOption(poll.getPollOptions(), request.getSelectedOption())) {
            throw new NotFoundException("Invalid poll option selected "+ request.getSelectedOption());
        }

        existingPollVote.setPoll(poll);
        existingPollVote.setUser(user);
        existingPollVote.setSelectedOption(request.getSelectedOption());
        pollVotesRepository.save(existingPollVote);
    }

    public void deletePollVote(Long id) {
        if (!pollVotesRepository.existsById(id)) {
            throw new NotFoundException("Poll vote not found with id: " + id);
        }
        pollVotesRepository.deleteById(id);
    }

    private boolean isValidPollOption(String[] pollOptions, String selectedOption) {
        if (pollOptions == null || pollOptions.length == 0 || selectedOption == null) {
            return false;
        }
        String normalizedSelected = selectedOption.trim();
        boolean isValid = Arrays.stream(pollOptions)
                .filter(option -> option != null)
                .map(String::trim)
                .anyMatch(option -> {
                    return option.equalsIgnoreCase(normalizedSelected);

                });
        return isValid;
    }
}