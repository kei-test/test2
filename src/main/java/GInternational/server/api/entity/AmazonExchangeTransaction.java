package GInternational.server.api.entity;


import GInternational.server.api.vo.AmazonTransactionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "amazon_exchange_transaction")
public class AmazonExchangeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_transaction_id")
    private Long id;
    private long lv;
    private String username;
    private String nickname;
    private String ip;
    private String distributor;

    @Column(name = "owner_name")
    private String ownerName;

    @Column(name = "bank_name")
    private String bankName;
    private String number;
    private String phone;

    @Column(name = "exchange_amount")
    private long exchangeAmount;  //  충전 신청 금액

    @Column(name = "amazon_bonus")
    private long amazonBonus;  //충전 승인시 보너스 금액

    @Column(name = "exchanged_count")
    private long exchangedCount;

    @Enumerated(EnumType.STRING)
    private AmazonTransactionEnum status; // 신청건에 대한 처리현황

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Builder
    public AmazonExchangeTransaction(Long id, int lv, String username, String nickname, String ownerName, String bankName, String number, String phone, String distributor, long exchangeAmount, int exchangedCount, String ip, AmazonTransactionEnum status, User user, Wallet wallet, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.lv = lv;
        this.username = username;
        this.nickname = nickname;
        this.ownerName = ownerName;
        this.bankName = bankName;
        this.number = number;
        this.phone = phone;
        this.distributor = distributor;
        this.exchangeAmount = exchangeAmount;
        this.exchangedCount = exchangedCount;
        this.ip = ip;
        this.status = status;
        this.user = user;
        this.wallet = wallet;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }
}

