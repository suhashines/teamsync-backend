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
@Table(name = "FeedPosts")
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
    private List<String> mediaUrls;

    @Column(nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    private LocalDate eventDate;

    @ElementCollection
    private List<String> pollOptions;

    @Column(nullable = false)
    private boolean isAiGenerated = false;

    private String aiSummary;

    public enum FeedPostType {
        text, photo, event, appreciation, poll, birthday, highlight
    }
}