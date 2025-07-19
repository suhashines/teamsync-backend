package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.FeedPosts;
import edu.teamsync.teamsync.entity.PollVotes;
import edu.teamsync.teamsync.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVotes, Long> {
    boolean existsByPollAndUser(FeedPosts poll, Users user);
}