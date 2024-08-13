package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "daily_limit")
public class DailyLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "limit_id")
    private Long id;

    @Column(name = "daily_article_limit", nullable = false)
    private int dailyArticleLimit; // 일일 게시판(CategoryName) 게시글 등록 가능 갯수

    @Column(name = "daily_comment_limit", nullable = false)
    private int dailyCommentLimit; // 일일 댓글 등록 가능 갯수

    @Column(name = "daily_article_point", nullable = false)
    private int dailyArticlePoint; // 게시글 등록시 마다 지급되는 포인트

    @Column(name = "daily_comment_point", nullable = false)
    private int dailyCommentPoint; // 댓글 등록시 마다 지급되는 포인트
}
