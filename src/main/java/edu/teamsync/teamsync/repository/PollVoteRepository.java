package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.PollVotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollVoteRepository extends JpaRepository<PollVotes, Long> {
    List<PollVotes> findByPoll_Id(Long pollId);
}