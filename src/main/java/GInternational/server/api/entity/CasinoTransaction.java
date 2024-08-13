package GInternational.server.api.entity;

import GInternational.server.api.vo.TransactionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "casino_transaction")
public class CasinoTransaction {
    //매퍼로 인해 Entity와 DTO의 필드명이 같아야함
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "casino_transaction_id")
    private Long id;
    @Column(name = "used_casino_balance")
    private long usedCasinoBalance;  //전환에 사용한 카지노머니
    @Column(name = "remaining_casino_balance")
    private long remainingCasinoBalance;  //현재 보유 카지노머니
    @Column(name = "used_sports_balance")
    private long usedSportsBalance;  //전환에 사용한 스포츠머니
    @Column(name = "remaining_sports_balance")
    private long remainingSportsBalance;  // 현재 보유 스포츠머니
    @Column(name = "exchanged_count")
    private long exchangedCount;
    private String ip;

    private String description;  //  ex: 포인트 적립
    private String note;   //비고

    @Enumerated(EnumType.STRING)
    private TransactionEnum status; // 신청건에 대한 처리현황


    @CreatedDate
    @Column(name = "processed_at", updatable = false, nullable = false)
    private LocalDateTime processedAt;  // 처리 시간

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public CasinoTransaction(Long id, long usedCasinoBalance, long usedSportsBalance, long remainingSportsBalance, long remainingCasinoBalance, String ip, long exchangedCount, String description, String note, LocalDateTime processedAt, TransactionEnum status, User user) {
        this.id = id;
        this.usedCasinoBalance = usedCasinoBalance;
        this.remainingCasinoBalance = remainingCasinoBalance;
        this.usedSportsBalance = usedSportsBalance;
        this.ip = ip;
        this.remainingSportsBalance = remainingSportsBalance;
        this.exchangedCount = exchangedCount;
        this.description = description;
        this.note = note;
        this.processedAt = processedAt;
        this.status = status;
        this.user = user;
    }




    public CasinoTransaction(Long id, long usedCasinoBalance, long remainingCasinoBalance, String description, LocalDateTime processedAt) {
        this.id = id;
        this.usedCasinoBalance = usedCasinoBalance;
        this.remainingCasinoBalance = remainingCasinoBalance;
        this.description = description;
        this.processedAt = processedAt;
    }
}
