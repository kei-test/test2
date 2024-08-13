package GInternational.server.api.entity;

import GInternational.server.api.vo.AmazonTransactionEnum;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "amazon_recharge_transaction")
public class AmazonRechargeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "amazon_recharge_transaction_id")
    private Long id;
    private long lv;
    private String username;
    private String nickname;

    @Column(name = "owner_name")
    private String ownerName;

    private String phone;

    @Column(name = "recharge_amount")
    private long rechargeAmount;  //  충전 신청 금액

    private long bonus;  //충전 승인시 보너스 금액

    @Column(name = "charged_count")
    private int chargedCount;

    private String ip;

    @Enumerated(EnumType.STRING)
    private AmazonTransactionEnum status; // 신청건에 대한 처리현황

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
    public AmazonRechargeTransaction(Long id, long lv, String username, String nickname, String ownerName, String phone, long rechargeAmount, long bonus, int chargedCount, String ip, AmazonTransactionEnum status, User user, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.lv = lv;
        this.username = username;
        this.nickname = nickname;
        this.ownerName = ownerName;
        this.phone = phone;
        this.rechargeAmount = rechargeAmount;
        this.bonus = bonus;
        this.chargedCount = chargedCount;
        this.ip = ip;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }


    public AmazonRechargeTransaction(Long id, long bonus, AmazonTransactionEnum status, LocalDateTime processedAt) {
        this.id = id;
        this.bonus = bonus;
        this.status = status;
        this.processedAt = processedAt;
    }
}
