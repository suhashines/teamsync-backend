package edu.teamsync.teamsync.controller;

import edu.teamsync.teamsync.dto.channelDTO.ChannelRequestDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelResponseDTO;
import edu.teamsync.teamsync.dto.channelDTO.ChannelUpdateDTO;
import edu.teamsync.teamsync.response.SuccessResponse;
import edu.teamsync.teamsync.service.ChannelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createChannel(@Valid @RequestBody ChannelRequestDTO requestDto) {
        channelService.createChannel(requestDto);
        SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                .code(HttpStatus.CREATED.value())
                .status(HttpStatus.CREATED)
                .message("Channel created successfully")
                .build();
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuccessResponse<ChannelResponseDTO>> getChannelById(@PathVariable Long id) {
        ChannelResponseDTO responseDto = channelService.getChannelById(id);
        SuccessResponse<ChannelResponseDTO> successResponse = SuccessResponse.<ChannelResponseDTO>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Channel retrieved successfully")
                .data(responseDto)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<List<ChannelResponseDTO>>> getAllChannels() {
        List<ChannelResponseDTO> channels = channelService.getAllChannels();
        SuccessResponse<List<ChannelResponseDTO>> successResponse = SuccessResponse.<List<ChannelResponseDTO>>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Channels retrieved successfully")
                .data(channels)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> updateChannel(@PathVariable Long id, @Valid @RequestBody ChannelUpdateDTO requestDto) {
         channelService.updateChannel(id, requestDto);
        SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK)
                .message("Channel updated successfully")
//                .data(responseDto)
                .build();
        return ResponseEntity.ok(successResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessResponse<Void>> deleteChannel(@PathVariable Long id) {
        channelService.deleteChannel(id);
        SuccessResponse<Void> successResponse = SuccessResponse.<Void>builder()
                .code(HttpStatus.NO_CONTENT.value())
                .status(HttpStatus.OK)
                .message("Channel deleted successfully")
                .build();
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}
