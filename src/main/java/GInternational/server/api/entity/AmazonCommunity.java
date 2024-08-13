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

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "amazon_community")
public class AmazonCommunity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amazon_community_id")
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_top")
    private Boolean isTop; //공지 상단고정 여부

    private String type; //구분
    private String nickname; // 작성자
    private String status;
    private String description;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;  // 처리 여부



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private AmazonCommunity parent;


    @JsonIgnore
    @OneToMany(mappedBy = "amazonCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AmazonComment> comments = new ArrayList<>();


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User writer;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "amazon_category_id")
    private AmazonCategory amazonCategory;
}
