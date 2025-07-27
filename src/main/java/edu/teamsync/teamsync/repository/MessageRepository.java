package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Messages, Long> {

    List<Messages> findByChannelIdOrderByTimestampAsc(Long channelId);

    Optional<Messages> findByIdAndChannelId(Long id, Long channelId);
    @Query("SELECT m FROM Messages m WHERE m.channel IS NULL AND " +
            "((m.sender.id = :senderId AND m.recipient.id = :recipientId) OR " +
            "(m.sender.id = :recipientId AND m.recipient.id = :senderId)) " +
            "ORDER BY m.timestamp ASC")
    List<Messages> findDirectMessagesBetweenUsers(@Param("senderId") Long senderId,
                                                  @Param("recipientId") Long recipientId);
}
