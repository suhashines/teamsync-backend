//package edu.teamsync.teamsync.controller;
//
//import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
//import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
//import edu.teamsync.teamsync.service.ReactionService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import jakarta.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/feedposts/{id}/reactions")
//@RequiredArgsConstructor
//public class ReactionController {
//
//    private final ReactionService reactionService;
//
//    @GetMapping
//    public ResponseEntity<List<ReactionResponseDTO>> getReactions(
//            @PathVariable Long id) {
//        List<ReactionResponseDTO> reactions= reactionService.getAllReactions(id);
//        return ResponseEntity.ok(reactions);
//    }
//
//    @PostMapping
//    public ResponseEntity<ReactionResponseDTO> addReaction(
//            @PathVariable Long id,
//            @Valid @RequestBody ReactionCreateRequestDTO request) {
//        ReactionResponseDTO reaction = reactionService.addReaction(id, request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(reaction);
//    }
//
//    @DeleteMapping
//    public ResponseEntity<Void> removeReaction(
//            @PathVariable Long id,
//            @RequestParam("user_id") Long userId,
//            @RequestParam("reaction_type") String reactionType) {
//        reactionService.removeReaction(id, userId, reactionType);
//        return ResponseEntity.noContent().build();
//    }
//}

package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.dto.reactionsDTO.ReactionCreateRequestDTO;
import edu.teamsync.teamsync.dto.reactionsDTO.ReactionResponseDTO;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/feedposts/{id}/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ReactionResponseDTO>>> getReactions(
            @PathVariable Long id) {
        List<ReactionResponseDTO> reactions = reactionService.getAllReactions(id);
        SuccessResponse<List<ReactionResponseDTO>> resp = SuccessResponse.<List<ReactionResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Reactions fetched successfully")
                .data(reactions)
                .build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> addReaction(
            @PathVariable Long id,
            @Valid @RequestBody ReactionCreateRequestDTO request) {
        reactionService.addReaction(id, request);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Reaction added successfully")
//                .data(reaction)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @DeleteMapping
    public ResponseEntity<SuccessResponse<Void>> removeReaction(
            @PathVariable Long id,
            @RequestParam("user_id") Long userId,
            @RequestParam("reaction_type") String reactionType) {
        reactionService.removeReaction(id, userId, reactionType);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Reaction removed successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
