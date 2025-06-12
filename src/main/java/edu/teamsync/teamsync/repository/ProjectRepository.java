package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.Projects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Projects, Long> {
}
