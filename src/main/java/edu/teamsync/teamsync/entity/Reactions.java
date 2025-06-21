package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "reactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reactions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(name="reaction_type",nullable = false)
    private ReactionType reactionType;

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @ManyToOne
    @JoinColumn(name = "post_id")
    private FeedPosts post;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private Comments comment;

    @ManyToOne
    @JoinColumn(name = "message_id")
    private Messages message;

    @PrePersist
    public void checkPostOrComment() {
        if ((post == null && comment == null) || (post != null && comment != null)) {
            throw new IllegalStateException("Exactly one of post_id or comment_id must be set");
        }
    }

    public enum ReactionType {
        like, love, haha, wow, sad, angry, celebrate, support, insightful
    }
}