package GInternational.server.api.entity;

import GInternational.server.api.vo.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "apple_results")
@EntityListeners(AuditingEntityListener.class)
public class AppleResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId; // 결과의 고유 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User userId; // 사용자의 ID

    @Column(name = "reward_name", nullable = false)
    private String rewardName; // 룰렛명

    @Column(name = "reward_value", nullable = false)
    private String rewardValue; // 상품값. 기프티콘 이름을 문자열로 저장하거나 포인트 값을 저장.

    @Column(name = "reward_description", nullable = false)
    private String rewardDescription; // 상품 세부 설명

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status; // 보상 지급 상태

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate; // 게임 결과가 최초 생성된 시간

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate; // 게임 결과가 마지막으로 수정된 시간
}
