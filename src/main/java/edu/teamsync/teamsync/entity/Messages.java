package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "Messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channels channel;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private Users recipient;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private ZonedDateTime timestamp = ZonedDateTime.now();

    @ManyToOne
    @JoinColumn(name = "thread_parent_id")
    private Messages threadParent;

    @PrePersist
    public void checkChannelOrRecipient() {
        if (channel == null && recipient == null) {
            throw new IllegalStateException("Either channel_id or recipient_id must be set");
        }
    }
}