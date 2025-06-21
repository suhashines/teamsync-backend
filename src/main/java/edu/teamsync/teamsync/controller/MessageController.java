
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<SuccessResponse<List<MessageResponseDTO>>> getChannelMessages(
            @PathVariable Long channelId) {
        List<MessageResponseDTO> messages = messageService.getChannelMessages(channelId);
        SuccessResponse<List<MessageResponseDTO>> resp = SuccessResponse.<List<MessageResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Messages fetched successfully")
                .data(messages)
                .build();
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<SuccessResponse<Void>> createChannelMessage(
            @PathVariable Long channelId,
            @Valid @RequestBody MessageCreationDTO requestDto) {
        messageService.createChannelMessage(channelId, requestDto);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Message created successfully")
//                .data(responseDto)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<SuccessResponse<MessageResponseDTO>> getChannelMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId) {
        MessageResponseDTO responseDto = messageService.getChannelMessage(channelId, messageId);
        SuccessResponse<MessageResponseDTO> resp = SuccessResponse.<MessageResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Message fetched successfully")
                .data(responseDto)
                .build();
        return ResponseEntity.ok(resp);
    }

    @PutMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<SuccessResponse<Void>> updateChannelMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId,
            @Valid @RequestBody MessageUpdateDTO requestDto) {
       messageService.updateChannelMessage(channelId, messageId, requestDto);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Message updated successfully")
//                .data(responseDto)
                .build();
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{channelId}/messages/{messageId}")
    public ResponseEntity<SuccessResponse<Void>> deleteChannelMessage(
            @PathVariable Long channelId,
            @PathVariable Long messageId) {
        messageService.deleteChannelMessage(channelId, messageId);
        SuccessResponse<Void> resp = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Message deleted successfully")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(resp);
    }
}
