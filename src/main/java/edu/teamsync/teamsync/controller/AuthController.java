package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.config.JwtProvider;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.UserException;
import edu.teamsync.teamsync.repository.UserRepository;
import edu.teamsync.teamsync.request.LoginRequest;
import edu.teamsync.teamsync.response.AuthResponse;
import edu.teamsync.teamsync.service.CustomerUserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private UserRepository userRepository;

    private JwtProvider jwtProvider;

    private PasswordEncoder passwordEncoder;

    private CustomerUserServiceImplementation customUserService;


    public AuthController(
            UserRepository userRepository,
            JwtProvider jwtProvider,
            PasswordEncoder passwordEncoder,
            CustomerUserServiceImplementation customUserService
    ) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.customUserService = customUserService;
    }


    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody Users user) throws UserException {

        String email = user.getEmail();
        String password = user.getPassword();
//        String firstString = user.getFirstName();
        String name = user.getName();

        Users isEmailExist = userRepository.findByEmail(email);

        if (isEmailExist != null) {
            throw new UserException("Email Is Already Used With Another Account");
        }

        Users createdUser = new Users();
        createdUser.setEmail(email);
        createdUser.setPassword(passwordEncoder.encode(password));
        createdUser.setName(name);


        Users savedUser = userRepository.save(createdUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(savedUser.getEmail(), savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse(token, "Signup Success");
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthResponse> loginUserHandler(@RequestBody LoginRequest loginRequest) {
//        try {
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse(token, "Signin Success");
//            return new ResponseEntity<>(new AuthResponse(token, "Login successful"), HttpStatus.OK);
        return new ResponseEntity<AuthResponse>(authResponse, HttpStatus.OK);

//        } catch (BadCredentialsException e) {
//            return new ResponseEntity<>(new AuthResponse(null, "Invalid credentials"), HttpStatus.UNAUTHORIZED);
//        }
    }

    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == "anonymousUser") {
            return new ResponseEntity<>(Map.of("message", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        String email = authentication.getName();
        Users user = userRepository.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(Map.of("message", "User not found"), HttpStatus.NOT_FOUND);
        }

        var response = new Object() {
            public String name = user.getName();
            public String profile_picture = user.getProfilePicture();
            public String designation = user.getDesignation();
        };

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid Username...");
        }

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid Password...");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}


