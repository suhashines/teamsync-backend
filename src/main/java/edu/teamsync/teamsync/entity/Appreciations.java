package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Table(name = "appreciations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appreciations {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_post_id")
    private FeedPosts parentPost;

    @ManyToOne
    @JoinColumn(name = "from_user_id", nullable = false)
    private Users fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user_id", nullable = false)
    private Users toUser;

    @Column(nullable = false)
    private String message;

    @Builder.Default
    @Column(nullable = false)
    private ZonedDateTime timestamp = ZonedDateTime.now();
}