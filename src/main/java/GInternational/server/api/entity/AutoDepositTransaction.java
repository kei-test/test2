package GInternational.server.api.entity;

import GInternational.server.api.vo.TransactionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

//금액적인 부분을 확인하는 테이블
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "auto_deposit_transaction")
public class AutoDepositTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_deposit_transaction_id")
    private Long id;
    private int lv;
    private String username;
    private String nickname;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "sports_balance")
    private long sportsBalance;

    private String distributor; //총판
    private long amount;  //  충전 신청 금액
    private long bonus;  //충전 승인시 보너스 금액
    @Enumerated(EnumType.STRING)
    private TransactionEnum status;  //처리 현황


    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;  //처리 일자
    @Column(name = "last_deposit_date")
    private LocalDateTime lastDepositDate;  //마지막 입금일
    @Column(name = "last_withdrawal_date")
    private LocalDateTime lastWithdrawalDate;  // 마지막 출금일
}
