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
@Entity(name = "amazon_comments")
public class AmazonComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amazon_comment_id")
    private Long id;

    private String title;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    private String type;

    private String status;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "amazon_community_id")
    private AmazonCommunity amazonCommunity;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    //기존 댓글에 대댓글 달 경우 부모 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AmazonComment parent;

    @JsonIgnore
    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<AmazonComment> children = new ArrayList<>();

    @Column(name = "children_count", nullable = false)
    @ColumnDefault("0")
    private long childrenCount;



    public void updateParent(AmazonComment amazonComment) {
        this.parent = amazonComment;
    }

    public void updateBoard(AmazonCommunity amazonCommunity) {
        this.amazonCommunity = amazonCommunity;
    }

    public void updateWriter(User user) {
        this.writer = user;
    }

    public void changeIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
