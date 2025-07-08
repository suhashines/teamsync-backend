package edu.teamsync.teamsync.services;

import edu.teamsync.teamsync.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private final String testEmail = "test@example.com";
    private final String testUserName = "John Doe";
    private final String testResetToken = "test-reset-token-123";
    private final String testFromEmail = "noreply@teamsync.com";
    private final String testFrontendUrl = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        // Set private fields using ReflectionTestUtils
        ReflectionTestUtils.setField(emailService, "fromEmail", testFromEmail);
        ReflectionTestUtils.setField(emailService, "frontendUrl", testFrontendUrl);
    }

    @Test
    void sendPasswordResetEmail_Success() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals(testFromEmail, capturedMessage.getFrom());
        assertEquals(testEmail, capturedMessage.getTo()[0]);
        assertEquals("Password Reset Request - TeamSync", capturedMessage.getSubject());

        String expectedResetUrl = testFrontendUrl + "/reset-password?token=" + testResetToken;
        String messageText = capturedMessage.getText();
        assertNotNull(messageText);
        assertTrue(messageText.contains(testUserName));
        assertTrue(messageText.contains(expectedResetUrl));
        assertTrue(messageText.contains("Hello " + testUserName));
        assertTrue(messageText.contains("TeamSync Team"));
        assertTrue(messageText.contains("This link will expire in 1 hour"));
        assertTrue(messageText.contains("If you did not request this password reset"));
    }

    @Test
    void sendPasswordResetEmail_WithCustomFrontendUrl() {
        // Arrange
        String customFrontendUrl = "https://app.teamsync.com";
        ReflectionTestUtils.setField(emailService, "frontendUrl", customFrontendUrl);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        String expectedResetUrl = customFrontendUrl + "/reset-password?token=" + testResetToken;
        String messageText = capturedMessage.getText();
        assertTrue(messageText.contains(expectedResetUrl));
    }

    @Test
    void sendPasswordResetEmail_MailSenderThrowsException() {
        // Arrange
        MailException mailException = new MailException("Failed to connect to mail server") {};
        doThrow(mailException).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName)
        );

        assertEquals("Failed to send password reset email", exception.getMessage());
        assertEquals(mailException, exception.getCause());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_WithNullParameters() {
        // Arrange - Mock mailSender to throw exception when send is called with invalid message
        doThrow(new IllegalArgumentException("Recipient cannot be null")).when(mailSender).send(any(SimpleMailMessage.class));

        // Test with null email
        RuntimeException exception1 = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail(null, testResetToken, testUserName)
        );
        assertEquals("Failed to send password reset email", exception1.getMessage());

        // Test with null token
        RuntimeException exception2 = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail(testEmail, null, testUserName)
        );
        assertEquals("Failed to send password reset email", exception2.getMessage());

        // Test with null username
        RuntimeException exception3 = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail(testEmail, testResetToken, null)
        );
        assertEquals("Failed to send password reset email", exception3.getMessage());

        // Verify that mailSender.send was called for each test
        verify(mailSender, times(3)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_WithEmptyParameters() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail("", testResetToken, "");

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage);
        assertEquals("", capturedMessage.getTo()[0]);

        String messageText = capturedMessage.getText();
        assertTrue(messageText.contains("Hello ,"));
    }

    @Test
    void sendPasswordResetEmail_MessageContentValidation() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        String messageText = capturedMessage.getText();

        // Verify message structure
        assertTrue(messageText.startsWith("Hello " + testUserName + ","));
        assertTrue(messageText.contains("You have requested to reset your password"));
        assertTrue(messageText.contains("Please click the following link"));
        assertTrue(messageText.contains("This link will expire in 1 hour"));
        assertTrue(messageText.contains("If you did not request this password reset"));
        assertTrue(messageText.endsWith("Best regards,\nTeamSync Team"));
    }

    @Test
    void sendPasswordResetEmail_ResetUrlFormatValidation() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        String specialCharToken = "token-with-special@chars#123";

        // Act
        emailService.sendPasswordResetEmail(testEmail, specialCharToken, testUserName);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        String expectedResetUrl = testFrontendUrl + "/reset-password?token=" + specialCharToken;
        String messageText = capturedMessage.getText();
        assertTrue(messageText.contains(expectedResetUrl));
    }

    @Test
    void sendPasswordResetEmail_MultipleInvocations() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName);
        emailService.sendPasswordResetEmail("another@example.com", "another-token", "Jane Doe");

        // Assert
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendPasswordResetEmail_VerifyAllMessageProperties() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();

        // Verify all message properties
        assertEquals(testFromEmail, capturedMessage.getFrom());
        assertArrayEquals(new String[]{testEmail}, capturedMessage.getTo());
        assertEquals("Password Reset Request - TeamSync", capturedMessage.getSubject());
        assertNotNull(capturedMessage.getText());
        assertNull(capturedMessage.getCc());
        assertNull(capturedMessage.getBcc());
        assertNull(capturedMessage.getReplyTo());
    }

    @Test
    void sendPasswordResetEmail_RuntimeExceptionWrapping() {
        // Arrange
        Exception originalException = new IllegalArgumentException("Invalid email format");
        doThrow(originalException).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                emailService.sendPasswordResetEmail(testEmail, testResetToken, testUserName)
        );

        assertEquals("Failed to send password reset email", exception.getMessage());
        assertEquals(originalException, exception.getCause());
        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}