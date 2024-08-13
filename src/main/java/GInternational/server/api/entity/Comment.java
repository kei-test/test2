package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "article_id")
    private Articles articles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "events_board_id")
    private EventsBoard eventsBoard;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    //기존 댓글에 대댓글 달 경우 부모 댓글
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent",orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>();

    @Column(name = "children_count",nullable = false)
    @ColumnDefault("0")
    private long childrenCount;



    public void updateParent(Comment comment) {
        this.parent = comment;
    }

    public void updateBoard(Articles articles) {
        this.articles = articles;
    }

    public void updateWriter(User user) {
        this.writer = user;
    }

    public void changeIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
