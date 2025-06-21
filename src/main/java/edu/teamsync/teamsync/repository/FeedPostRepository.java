package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.FeedPosts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedPostRepository extends JpaRepository<FeedPosts, Long> {
}
