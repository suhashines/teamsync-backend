
package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.dto.messageDTO.MessageCreationDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageResponseDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageUpdateDTO;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping()
    public ResponseEntity<SuccessResponse<Void>> createMessage(
            @Valid @RequestBody MessageCreationDTO requestDto) {
        messageService.createMessage(requestDto);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Message created successfully")
//                .data(responseDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping()
    public ResponseEntity<SuccessResponse<List<MessageResponseDTO>>> getMessages(
            @RequestParam(required = false) Long channelId,
            @RequestParam(required = false) Long recipientId) {

        List<MessageResponseDTO> messages = messageService.getMessages(channelId, recipientId);
        SuccessResponse<List<MessageResponseDTO>> resp = SuccessResponse.<List<MessageResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Messages fetched successfully")
                .data(messages)
                .build();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{messageId}")
    public ResponseEntity<SuccessResponse<Void>> updateMessage(
            @PathVariable Long messageId,
            @Valid @RequestBody MessageUpdateDTO requestDto) {
       messageService.updateMessage(messageId, requestDto);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Message updated successfully")
//                .data(responseDto)
                .build();
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{messageId}")
    public ResponseEntity<SuccessResponse<Void>> deleteMessage(
            @PathVariable Long messageId) {
        messageService.deleteMessage(messageId);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Message deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}