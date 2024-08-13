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
@Entity(name = "exchange_transaction")
public class ExchangeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_transaction_id")
    private Long id;
    private int lv;
    private String username;
    private String nickname;
    @Column(name = "owner_name")
    private String ownerName;
    @Column(name = "bank_name")
    private String bankName;
    private String number;
    private String phone;
    private String site;
    @Column(name = "exchange_amount")
    private long exchangeAmount;  //  환전 신청 금액
    @Column(name = "remaining_sports_balance")
    private long remainingSportsBalance; // 충전 승인 처리 후 스포츠머니
    private int bonus;
    @Column(name = "remaining_point")
    private int remainingPoint;
    @Column(name = "exchanged_count")
    private int exchangedCount;
    private String ip;


    @Enumerated(EnumType.STRING)
    private TransactionEnum status; // 신청건에 대한 처리현황

    @Enumerated(EnumType.STRING)
    private TransactionGubunEnum gubun;  //충전 시 충전대상 구분

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
    public ExchangeTransaction(Long id, int lv, String username, String nickname, String ownerName, String bankName, String number, String phone, String site, long exchangeAmount, long remainingSportsBalance, int exchangedCount, int bonus, int remainingPoint, String ip, TransactionEnum status, TransactionGubunEnum gubun, User user, Wallet wallet, LocalDateTime createdAt, LocalDateTime processedAt) {
        this.id = id;
        this.lv = lv;
        this.username = username;
        this.nickname = nickname;
        this.ownerName = ownerName;
        this.bankName = bankName;
        this.number = number;
        this.phone = phone;
        this.site = site;
        this.exchangeAmount = exchangeAmount;
        this.remainingSportsBalance = remainingSportsBalance;
        this.exchangedCount = exchangedCount;
        this.bonus = bonus;
        this.remainingPoint = remainingPoint;
        this.ip = ip;
        this.status = status;
        this.gubun = gubun;
        this.user = user;
        this.wallet = wallet;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
    }
}

