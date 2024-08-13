package GInternational.server.api.entity;

import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.TransactionGubunEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "recharge_transaction")
public class RechargeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recharge_transaction_id")
    private Long id;
    private int lv;
    private String username;
    private String nickname;
    @Column(name = "owner_name")
    private String ownerName;
    private String phone;
    private String site; // 사이트(윈드, 메가 등)
    @Column(name = "recharge_amount")
    private long rechargeAmount;  // 충전 신청 금액
    @Column(name = "remaining_sports_balance")
    private long remainingSportsBalance; // 충전 승인 처리 후 스포츠머니
    private int bonus;  // 충전 승인시 보너스 금액
    @Column(name = "remaining_point")
    private int remainingPoint; // 충전 승인 처리 후 포인트
    @Column(name = "charged_count")
    private int chargedCount;
    @Column(name = "today_charged_count")
    private int todayChargedCount = 0;
    @Column(name = "is_bonus_overridden")
    private boolean isBonusOverridden;
    private String ip;

    private String message;

    @Column(name = "is_first_recharge")
    private boolean isFirstRecharge; // 첫충전 여부

    @Enumerated(EnumType.STRING)
    private TransactionEnum status; // 신청건에 대한 처리현황

    @Enumerated(EnumType.STRING)
    private TransactionGubunEnum gubun;  //충전 시 충전대상 구분

    @Column(name = "depositor")
    private String depositor = ""; // 자동충전 입금자

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;


    @Builder
    public RechargeTransaction(Long id, int lv, String username, String nickname, String ownerName, String phone, long rechargeAmount, long remainingSportsBalance, int bonus, int remainingPoint, int chargedCount, String ip, TransactionEnum status, TransactionGubunEnum gubun, String site, String message, String depositor, User user, LocalDateTime createdAt, LocalDateTime processedAt, boolean isFirstRecharge) {
        this.id = id;
        this.lv = lv;
        this.username = username;
        this.nickname = nickname;
        this.ownerName = ownerName;
        this.phone = phone;
        this.rechargeAmount = rechargeAmount;
        this.remainingSportsBalance = remainingSportsBalance;
        this.bonus = bonus;
        this.remainingPoint = remainingPoint;
        this.chargedCount = chargedCount;
        this.ip = ip;
        this.status = status;
        this.gubun = gubun;
        this.site = site;
        this.message = message;
        this.depositor = depositor;
        this.user = user;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.isFirstRecharge = isFirstRecharge;
    }


    public RechargeTransaction(Long id, int rechargeAmount, int bonus, TransactionEnum status, LocalDateTime processedAt) {
        this.id = id;
        this.bonus = bonus;
        this.status = status;
        this.processedAt = processedAt;
    }
}
