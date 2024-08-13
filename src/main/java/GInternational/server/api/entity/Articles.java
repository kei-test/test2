package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "articles")
public class Articles extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;
    private String title;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_top")
    private Boolean isTop;
    @Column(name = "comment_allowed", nullable = false)
    private boolean commentAllowed = true;

    //이전 글, 다음 글 id를 담을 필드
    @Column(name = "previous_article_id")
    private Long previousArticleId;
    @Column(name = "next_article_id")
    private Long nextArticleId;

    private String site = "test";

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @JsonIgnore
    @OneToMany(mappedBy = "articles", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Articles(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //로그인 문의를 위한 컬럼
    @Column(name = "writer_name")
    private String writerName;  // username
    @Column(name = "owner_name")
    private String ownerName;   // 예금주
    private String nickname;    // 닉네임
    private String phone;       // 핸드폰
    private String ip;          // 요청자의 ip
    private String memo = "";   // 로그인 문의 "메모"

    // 어드민페이지 고객센터를 위한 컬럼
    @Column(name = "read_count")
    private int readCount;       // 게시글 조회수 getMyArticle로 조회할 때 마다 +1 증가
    @Column(name = "answer_status")
    private String answerStatus; // 답변 상태를 위한 필드(로그인 문의 포함) // 답변대기, 답변완료

    // 어드민페이지 게시판을 위한 컬럼
    @Column(name = "comment_count")
    private int commentCount;  // 댓글 달릴때 마다 1 증가
    @Column(name = "view_status")
    private String viewStatus; // 노출, 비노출 // 비노출일때 유저페이지에서 게시글 조회 안됨.

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
