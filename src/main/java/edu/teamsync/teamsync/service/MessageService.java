package edu.teamsync.teamsync.service;

import edu.teamsync.teamsync.dto.messageDTO.MessageCreationDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageResponseDTO;
import edu.teamsync.teamsync.dto.messageDTO.MessageUpdateDTO;
import edu.teamsync.teamsync.entity.Channels;
import edu.teamsync.teamsync.entity.Messages;
import edu.teamsync.teamsync.entity.Users;
import edu.teamsync.teamsync.exception.http.NotFoundException;
import edu.teamsync.teamsync.mapper.MessageMapper;
import edu.teamsync.teamsync.repository.ChannelRepository;
import edu.teamsync.teamsync.repository.MessageRepository;
import edu.teamsync.teamsync.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageMapper messageMapper;

    public List<MessageResponseDTO> getChannelMessages(Long channelId) {
        // Validate channel exists
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel with ID " + channelId + " not found");
        }

        List<Messages> messages = messageRepository.findByChannelIdOrderByTimestampAsc(channelId);
        return messages.stream()
                .map(messageMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public void createChannelMessage(Long channelId, MessageCreationDTO requestDto) {
        // Validate channel exists
        Channels channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel with ID " + channelId + " not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Users sender = userRepository.findByEmail(email);
        if (sender == null) {
            throw new NotFoundException("User not found with email "+email);
        }

        // Validate recipient if provided
        Users recipient = null;
        if (requestDto.recipientId() != null) {
            recipient = userRepository.findById(requestDto.recipientId())
                    .orElseThrow(() -> new NotFoundException("Recipient with ID " + requestDto.recipientId() + " not found"));
        }

        // Validate thread parent if provided
        Messages threadParent = null;
        if (requestDto.threadParentId() != null) {
            threadParent = messageRepository.findById(requestDto.threadParentId())
                    .orElseThrow(() -> new NotFoundException("Thread parent message with ID " + requestDto.threadParentId() + " not found"));
        }

        Messages message = Messages.builder()
                .content(requestDto.content())
                .sender(sender)
                .channel(channel)
                .recipient(recipient)
                .threadParent(threadParent)
                .timestamp(ZonedDateTime.now())
                .build();

        messageRepository.save(message);
//        return messageMapper.toDto(savedMessage);
    }

    public MessageResponseDTO getChannelMessage(Long channelId, Long messageId) {
        // Validate channel exists
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel with ID " + channelId + " not found");
        }

        // Find message by ID and channel ID
        Messages message = messageRepository.findByIdAndChannelId(messageId, channelId)
                .orElseThrow(() -> new NotFoundException("Message with ID " + messageId + " not found in channel " + channelId));

        return messageMapper.toDto(message);
    }

    @Transactional
    public void updateChannelMessage(Long channelId, Long messageId, MessageUpdateDTO requestDto) {
        // Validate channel exists
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel with ID " + channelId + " not found");
        }

        // Validate message exists in the channel
        Messages existingMessage = messageRepository.findByIdAndChannelId(messageId, channelId)
                .orElseThrow(() -> new NotFoundException("Message with ID " + messageId + " not found in channel " + channelId));

        // Validate sender exists
        Users sender = userRepository.findById(requestDto.senderId())
                .orElseThrow(() -> new NotFoundException("Sender with ID " + requestDto.senderId() + " not found"));

        // Validate channel in request body matches path channel
        Channels channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NotFoundException("Channel with ID " + channelId + " not found"));

        // Validate recipient if provided
        Users recipient = null;
        if (requestDto.recipientId() != null) {
            recipient = userRepository.findById(requestDto.recipientId())
                    .orElseThrow(() -> new NotFoundException("Recipient with ID " + requestDto.recipientId() + " not found"));
        }

        // Validate thread parent if provided
        Messages threadParent = null;
        if (requestDto.threadParentId() != null) {
            threadParent = messageRepository.findById(requestDto.threadParentId())
                    .orElseThrow(() -> new NotFoundException("Thread parent message with ID " + requestDto.threadParentId() + " not found"));
        }

        Messages message = messageMapper.toEntity(requestDto);
        message.setSender(sender);
        message.setChannel(channel);
        message.setRecipient(recipient);
        message.setThreadParent(threadParent);

        // Save updated message
         messageRepository.save(message);

        // Map entity to response DTO
//        return messageMapper.toDto(savedMessage);
    }

    @Transactional
    public void deleteChannelMessage(Long channelId, Long messageId) {
        // Validate channel exists
        if (!channelRepository.existsById(channelId)) {
            throw new NotFoundException("Channel with ID " + channelId + " not found");
        }

        // Validate message exists in the channel
        Messages message = messageRepository.findByIdAndChannelId(messageId, channelId)
                .orElseThrow(() -> new NotFoundException("Message with ID " + messageId + " not found in channel " + channelId));

        messageRepository.delete(message);
    }
}