package edu.teamsync.teamsync.repository;

import edu.teamsync.teamsync.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
    public Users findByEmail(String email);
}



