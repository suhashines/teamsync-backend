package edu.teamsync.teamsync.controller;


import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.UserException;
import edu.teamsync.teamsync.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Users user = userRepository.findById(id)
                    .orElseThrow(() -> new UserException("User not found with id: " + id));

            // Construct the response object matching the example JSON
            var response = new Object() {
                public Long id = user.getId();
                public String name = user.getName();
                public String email = user.getEmail();
                public String profilePicture = user.getProfilePicture();
                public String designation = user.getDesignation();
                public String birthdate = user.getBirthdate() != null ? user.getBirthdate().toString() : null;
                public String joinDate = user.getJoinDate() != null ? user.getJoinDate().toString() : null;
                public Boolean predictedBurnoutRisk = user.getPredictedBurnoutRisk();
                public String message = "Success";
            };

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(new Object() {
                public String message = e.getMessage();
            }, HttpStatus.NOT_FOUND);
        }
    }
}
