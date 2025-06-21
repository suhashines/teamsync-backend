package edu.teamsync.teamsync.repository;


import edu.teamsync.teamsync.entity.Channels;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channels, Long> {
}