//package edu.teamsync.teamsync.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//@Entity
//@Table(name = "channels")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Channels {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ChannelType type;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Projects project;
//
//    @ElementCollection
//    private List<Long> members;
//
//    public enum ChannelType {
//        direct, group
//    }
//}


//package edu.teamsync.teamsync.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//
//@Entity
//@Table(name = "channels")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Channels {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false)
//    private String name;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ChannelType type;
//
//    @ManyToOne
//    @JoinColumn(name = "project_id")
//    private Projects project;
//
//    @ManyToMany
//    @JoinTable(
//            name = "channels_members",
//            joinColumns = @JoinColumn(name = "channel_id"),
//            inverseJoinColumns = @JoinColumn(name = "user_id")
//    )
//    private List<Users> members;
//
//    public enum ChannelType {
//        direct, group
//    }
//}

package edu.teamsync.teamsync.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "channels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Channels {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Projects project;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "members", columnDefinition = "bigint[]")
    private List<Long> members;

    public enum ChannelType {
        direct, group
    }
}