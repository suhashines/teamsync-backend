package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.Appreciations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppreciationRepository extends JpaRepository<Appreciations, Long> {
}