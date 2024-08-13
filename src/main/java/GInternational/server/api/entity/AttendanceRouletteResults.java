package GInternational.server.api.entity;

import GInternational.server.api.vo.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "attendance_roulette_results")
@EntityListeners(AuditingEntityListener.class)
public class AttendanceRouletteResults {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long resultId; // 결과의 고유 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User userId; // 룰렛을 돌린 사용자의 ID

    @Column(name = "roulette_name", nullable = false)
    private String rouletteName; // 룰렛명

    @Column(name = "reward_value", nullable = false)
    private String rewardValue; // 상품값. 기프티콘 이름을 문자열로 저장하거나 포인트 값을 저장.

    @Column(name = "spin_date", nullable = false)
    private LocalDateTime spinDate; // 룰렛을 돌린 날짜 및 시간

    @Column(name = "reward_description")
    private String rewardDescription; // 상품 세부 설명

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status; // 보상 지급 상태

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}
