package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pollvotes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PollVotes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    private FeedPosts poll; // poll_id -> findByPoll_Id(Long id)
    //  List<PollVotes> votes = findByPoll_Id(poll_id)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  //Many PollVotes to one user , FeedPost : ajke amra ki khabo? post id=1 , riyad(id=2,post_id=1)-> mangsho, riyad(2,post_id=3)
    private Users user; // findByUser_NameAndDesignation // user_id(users), post_id(feedposts) , option

    @Column(name = "selected_option", nullable = false)
    private String selectedOption;
}