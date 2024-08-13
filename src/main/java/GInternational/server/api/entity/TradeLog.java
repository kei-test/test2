package GInternational.server.api.entity;

import GInternational.server.api.vo.TradeLogCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "trade_log")
public class TradeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_log_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "processed_id")
    private Long processedId; // 처리자
    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 처리시간

    private String username; // 멤버Id
    private String nickName; // 닉네임
    private String role; // 멤버 구분

    @Column(name = "first_amazon_money")
    private long firstAmazonMoney; // 변동전 머니
    @Column(name = "amazon_money")
    private long amazonMoney; // 처리금액
    @Column(name = "final_amazon_money")
    private long finalAmazonMoney; // 변동후 머니

    @Column(name = "first_amazon_point")
    private long firstAmazonPoint; // 변동전 포인트
    @Column(name = "amazon_point")
    private long amazonPoint; // 포인트
    @Column(name = "final_amazon_point")
    private long finalAmazonPoint; // 최종 포인트

    private String bigo; // 비고

    @Enumerated(EnumType.STRING)
    private TradeLogCategory category; // 머니, 포인트
}