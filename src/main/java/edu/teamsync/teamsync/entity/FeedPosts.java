package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "feedposts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedPosts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedPostType type;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @Column(nullable = false)
    private String content;

    @ElementCollection
    @Column(name = "media_url")
    private List<String> mediaUrls;


    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();
    @Column(name = "event_date")
    private LocalDate eventDate;

    @ElementCollection
    @Column(name = "poll_option")
    private List<String> pollOptions;

    @Column(name = "is_ai_generated", nullable = false)
    private boolean isAiGenerated = false;
    @Column(name = "ai_summary")
    private String aiSummary;

    public enum FeedPostType {
        text, photo, event, appreciation, poll, birthday, highlight
    }
}