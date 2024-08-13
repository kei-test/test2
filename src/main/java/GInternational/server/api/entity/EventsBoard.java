package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "events_board")
public class EventsBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "events_board_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @JsonIgnore
    @OneToMany(mappedBy = "eventsBoard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Column(name = "writer_username")
    private String writerUsername;
    @Column(name = "read_count")
    private Integer readCount = 0;
    @Column(name = "comment_count")
    private Integer commentCount = 0;

    @Column(name = "view_status")
    private String viewStatus; // 노출, 비노출 // 비노출일때 유저페이지에서 게시글 조회 안됨.

    private String site = "test";

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, name = "start_date")
    private LocalDateTime startDate;

    @Column(nullable = false, name = "end_date")
    private LocalDateTime endDate;

    // 댓글 수 증가
    public void incrementCommentCount() {
        this.commentCount += 1;
    }

    // 댓글 수 감소
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount -= 1;
        }
    }
}
