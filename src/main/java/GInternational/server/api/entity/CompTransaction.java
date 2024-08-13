package GInternational.server.api.entity;

import GInternational.server.api.vo.RollingTransactionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "comp_transaction")
public class CompTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comp_transaction_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    private Long userId;

    private int lv;
    private String username;
    private String nickname;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 처리 시간

    @Column(name = "last_day_charge_sports_balance")
    private BigDecimal lastDayChargeSportsBalance; // 전일 충전한 캐시
    @Column(name = "calculated_reward")
    private BigDecimal calculatedReward; // 계산된 적립 포인트 (전일 환전한 카지노 머니의 300%)

    @Column(precision = 19, scale = 4)
    private BigDecimal rate; // 지급 퍼센트

    @Column(name = "last_day_amount")
    private BigDecimal lastDayAmount; // 전날 베팅 금액
    @Column(name = "sports_balance")
    private long sportsBalance;  // 스포츠 머니
    @Column(name = "casino_balance")
    private long casinoBalance;  // 카지노 머니
    @Enumerated(EnumType.STRING)
    private RollingTransactionEnum status; // 신청건에 대한 처리현황
    @Column(name = "user_ip")
    private String userIp;

    private String distributor; // 매장
}
