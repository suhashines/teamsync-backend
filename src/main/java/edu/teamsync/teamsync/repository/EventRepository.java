package edu.teamsync.teamsync.repository;


import edu.teamsync.teamsync.entity.Events;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
}