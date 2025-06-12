package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationCreateDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationResponseDTO;
import edu.teamsync.teamsync.dto.appreciationsDTO.AppreciationUpdateDTO;
import edu.teamsync.teamsync.entity.Appreciations;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.exception.http.UnauthorizedException;
import edu.teamsync.teamsync.mapper.AppreciationMapper;
import edu.teamsync.teamsync.repository.AppreciationRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppreciationService {

    private final AppreciationRepository appreciationRepository;
    private final UserRepository userRepository;
    private final AppreciationMapper appreciationMapper;

    public List<AppreciationResponseDTO> getAllAppreciations() {
        List<Appreciations> appreciations = appreciationRepository.findAll();

        return appreciations.stream()
                .map(appreciationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AppreciationResponseDTO getAppreciationById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appreciation ID cannot be null");
        }

        Appreciations appreciation = appreciationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appreciation not found with id: " + id));
        return appreciationMapper.toResponseDTO(appreciation);
    }

    public void createAppreciation(AppreciationCreateDTO createDTO, String userEmail) {
        if (createDTO == null) {
            throw new IllegalArgumentException("Appreciation data cannot be null");
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new UnauthorizedException("User email is required for creating appreciation");
        }

        // Validate from user (authenticated user)
        Users fromUser = userRepository.findByEmail(userEmail);
        if (fromUser == null) {
            throw new UnauthorizedException("User not found with email: " + userEmail);
        }

        // Validate to user exists
        if (createDTO.getToUserId() == null) {
            throw new IllegalArgumentException("Recipient user ID cannot be null");
        }

        Users toUser = userRepository.findById(createDTO.getToUserId())
                .orElseThrow(() -> new NotFoundException("Recipient user not found with id: " + createDTO.getToUserId()));

        // Prevent self-appreciation
        if (fromUser.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("Cannot create appreciation for yourself");
        }

        Appreciations appreciation = appreciationMapper.toEntity(createDTO);
        appreciation.setFromUser(fromUser);
        appreciation.setToUser(toUser);
        appreciation.setTimestamp(ZonedDateTime.now());

        appreciationRepository.save(appreciation);

    }

    public void updateAppreciation(Long id, AppreciationUpdateDTO updateDTO, String userEmail) {
        if (id == null) {
            throw new IllegalArgumentException("Appreciation ID cannot be null");
        }

        if (updateDTO == null) {
            throw new IllegalArgumentException("Update data cannot be null");
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new UnauthorizedException("User email is required for updating appreciation");
        }

        Appreciations existingAppreciation = appreciationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appreciation not found with id: " + id));

        // Check if the authenticated user is the creator of the appreciation
        Users authenticatedUser = userRepository.findByEmail(userEmail);
        if (authenticatedUser == null) {
            throw new UnauthorizedException("User not found with email: " + userEmail);
        }

//        if (!existingAppreciation.getFromUser().getId().equals(authenticatedUser.getId())) {
//            throw new SecurityException("You can only update appreciations that you created");
//        }

        // Validate users exist if IDs are being updated
        if (updateDTO.getFromUserId() != null) {
            Users fromUser = userRepository.findById(updateDTO.getFromUserId())
                    .orElseThrow(() -> new NotFoundException("From user not found with id: " + updateDTO.getFromUserId()));
            existingAppreciation.setFromUser(fromUser);
        }

        if (updateDTO.getToUserId() != null) {
            Users toUser = userRepository.findById(updateDTO.getToUserId())
                    .orElseThrow(() -> new NotFoundException("To user not found with id: " + updateDTO.getToUserId()));

            // Prevent self-appreciation
            if (existingAppreciation.getFromUser().getId().equals(toUser.getId())) {
                throw new IllegalArgumentException("Cannot create appreciation for yourself");
            }

            existingAppreciation.setToUser(toUser);
        }

        // Update the entity with new values
        appreciationMapper.updateEntityFromDTO(updateDTO, existingAppreciation);
        appreciationRepository.save(existingAppreciation);
    }

    public void deleteAppreciation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Appreciation ID cannot be null");
        }

        appreciationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appreciation not found with id: " + id));

        appreciationRepository.deleteById(id);
    }
}