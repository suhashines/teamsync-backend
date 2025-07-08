package edu.teamsync.teamsync.services;

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
import edu.teamsync.teamsync.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageMapper messageMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private MessageService messageService;

    private final Long channelId = 1L;
    private final Long messageId = 1L;
    private final Long recipientId = 2L;
    private final Long threadParentId = 3L;
    private final String userEmail = "test@example.com";
    private final Long userId = 1L;

    private Channels channel;
    private Users sender;
    private Users recipient;
    private Messages message;
    private Messages threadParent;
    private MessageCreationDTO creationDTO;
    private MessageUpdateDTO updateDTO;
    private MessageResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        channel = Channels.builder().id(channelId).build();
        sender = Users.builder().id(userId).email(userEmail).build();
        recipient = Users.builder().id(recipientId).email("recipient@example.com").build();

        threadParent = Messages.builder()
                .id(threadParentId)
                .content("Parent message")
                .sender(sender)
                .channel(channel)
                .timestamp(ZonedDateTime.now())
                .build();

        message = Messages.builder()
                .id(messageId)
                .content("Test message")
                .sender(sender)
                .channel(channel)
                .recipient(recipient)
                .threadParent(threadParent)
                .timestamp(ZonedDateTime.now())
                .build();

        creationDTO = new MessageCreationDTO(
                "New message content",
                channelId,
                recipientId,
                threadParentId
        );

        updateDTO = new MessageUpdateDTO(
                channelId,
                recipientId,
                "Updated message content"
        );

        responseDTO = new MessageResponseDTO(
                messageId,
                userId,
                channelId,
                recipientId,
                "Test message",
                message.getTimestamp(),
                threadParentId
        );
    }

    @Test
    void getChannelMessages_Success() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByChannelIdOrderByTimestampAsc(channelId))
                .thenReturn(Collections.singletonList(message));
        when(messageMapper.toDto(message)).thenReturn(responseDTO);

        List<MessageResponseDTO> result = messageService.getChannelMessages(channelId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(messageId, result.get(0).id());
        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByChannelIdOrderByTimestampAsc(channelId);
        verify(messageMapper).toDto(message);
    }

    @Test
    void getChannelMessages_ChannelNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> messageService.getChannelMessages(channelId));
        verify(channelRepository).existsById(channelId);
        verifyNoInteractions(messageRepository, messageMapper);
    }

    @Test
    void createChannelMessage_Success() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(userEmail);
            when(userRepository.findByEmail(userEmail)).thenReturn(sender);
            when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
            when(messageRepository.findById(threadParentId)).thenReturn(Optional.of(threadParent));
            when(messageRepository.save(any(Messages.class))).thenReturn(message);

            messageService.createChannelMessage(channelId, creationDTO);

            verify(channelRepository).findById(channelId);
            verify(userRepository).findByEmail(userEmail);
            verify(userRepository).findById(recipientId);
            verify(messageRepository).findById(threadParentId);
            verify(messageRepository).save(any(Messages.class));
        }
    }

    @Test
    void createChannelMessage_ChannelNotFound() {
        when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> messageService.createChannelMessage(channelId, creationDTO));
        verify(channelRepository).findById(channelId);
        verifyNoInteractions(userRepository, messageRepository);
    }

    @Test
    void createChannelMessage_SenderNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(userEmail);
            when(userRepository.findByEmail(userEmail)).thenReturn(null);

            assertThrows(NotFoundException.class, () -> messageService.createChannelMessage(channelId, creationDTO));
            verify(channelRepository).findById(channelId);
            verify(userRepository).findByEmail(userEmail);
        }
    }

    @Test
    void createChannelMessage_RecipientNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(userEmail);
            when(userRepository.findByEmail(userEmail)).thenReturn(sender);
            when(userRepository.findById(recipientId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> messageService.createChannelMessage(channelId, creationDTO));
            verify(channelRepository).findById(channelId);
            verify(userRepository).findByEmail(userEmail);
            verify(userRepository).findById(recipientId);
        }
    }

    @Test
    void createChannelMessage_ThreadParentNotFound() {
        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(userEmail);
            when(userRepository.findByEmail(userEmail)).thenReturn(sender);
            when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
            when(messageRepository.findById(threadParentId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> messageService.createChannelMessage(channelId, creationDTO));
            verify(channelRepository).findById(channelId);
            verify(userRepository).findByEmail(userEmail);
            verify(userRepository).findById(recipientId);
            verify(messageRepository).findById(threadParentId);
        }
    }

    @Test
    void createChannelMessage_WithoutOptionalFields() {
        MessageCreationDTO simplifiedDTO = new MessageCreationDTO(
                "Simple message",
                channelId,
                null, // no recipient
                null  // no thread parent
        );

        try (MockedStatic<SecurityContextHolder> mockedSecurityContextHolder = mockStatic(SecurityContextHolder.class)) {
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
            mockedSecurityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn(userEmail);
            when(userRepository.findByEmail(userEmail)).thenReturn(sender);
            when(messageRepository.save(any(Messages.class))).thenReturn(message);

            messageService.createChannelMessage(channelId, simplifiedDTO);

            verify(channelRepository).findById(channelId);
            verify(userRepository).findByEmail(userEmail);
            verify(messageRepository).save(any(Messages.class));
            verifyNoMoreInteractions(userRepository, messageRepository);
        }
    }

    @Test
    void getChannelMessage_Success() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.of(message));
        when(messageMapper.toDto(message)).thenReturn(responseDTO);

        MessageResponseDTO result = messageService.getChannelMessage(channelId, messageId);

        assertNotNull(result);
        assertEquals(messageId, result.id());
        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verify(messageMapper).toDto(message);
    }

    @Test
    void getChannelMessage_ChannelNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> messageService.getChannelMessage(channelId, messageId));
        verify(channelRepository).existsById(channelId);
        verifyNoInteractions(messageRepository, messageMapper);
    }

    @Test
    void getChannelMessage_MessageNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> messageService.getChannelMessage(channelId, messageId));
        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verifyNoInteractions(messageMapper);
    }

    @Test
    void updateChannelMessage_Success() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.of(message));
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(userRepository.findById(recipientId)).thenReturn(Optional.of(recipient));
        when(messageRepository.save(any(Messages.class))).thenReturn(message);

        messageService.updateChannelMessage(channelId, messageId, updateDTO, userEmail);

        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verify(channelRepository).findById(channelId);
        verify(userRepository).findById(recipientId);
        verify(messageRepository).save(any(Messages.class));
    }

    @Test
    void updateChannelMessage_ChannelNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                messageService.updateChannelMessage(channelId, messageId, updateDTO, userEmail));
        verify(channelRepository).existsById(channelId);
    }

    @Test
    void updateChannelMessage_MessageNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                messageService.updateChannelMessage(channelId, messageId, updateDTO, userEmail));
        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
    }

    @Test
    void updateChannelMessage_RecipientNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.of(message));
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(userRepository.findById(recipientId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                messageService.updateChannelMessage(channelId, messageId, updateDTO, userEmail));
        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verify(channelRepository).findById(channelId);
        verify(userRepository).findById(recipientId);
    }

    @Test
    void updateChannelMessage_WithoutRecipient() {
        MessageUpdateDTO updateDTOWithoutRecipient = new MessageUpdateDTO(
                channelId,
                null, // no recipient
                "Updated content"
        );

        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.of(message));
        when(channelRepository.findById(channelId)).thenReturn(Optional.of(channel));
        when(messageRepository.save(any(Messages.class))).thenReturn(message);

        messageService.updateChannelMessage(channelId, messageId, updateDTOWithoutRecipient, userEmail);

        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verify(channelRepository).findById(channelId);
        verify(messageRepository).save(any(Messages.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void deleteChannelMessage_Success() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.of(message));

        messageService.deleteChannelMessage(channelId, messageId);

        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verify(messageRepository).delete(message);
    }

    @Test
    void deleteChannelMessage_ChannelNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(false);

        assertThrows(NotFoundException.class, () ->
                messageService.deleteChannelMessage(channelId, messageId));
        verify(channelRepository).existsById(channelId);
        verifyNoInteractions(messageRepository);
    }

    @Test
    void deleteChannelMessage_MessageNotFound() {
        when(channelRepository.existsById(channelId)).thenReturn(true);
        when(messageRepository.findByIdAndChannelId(messageId, channelId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                messageService.deleteChannelMessage(channelId, messageId));
        verify(channelRepository).existsById(channelId);
        verify(messageRepository).findByIdAndChannelId(messageId, channelId);
        verify(messageRepository, never()).delete(any());
    }
}
