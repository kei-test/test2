package GInternational.server.api.entity;


import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "wallet")
public class Wallet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long id;
    @Column(name = "number")
    private Long number;         // 계좌번호
    @Column(name = "bank_password", nullable = false)
    private String bankPassword; // 환전 비밀번호
    @Column(name = "bank_name")
    private String bankName;     // 은행명
    @Column(name = "owner_name")
    private String ownerName;    // 예금주
    @Column(name = "sports_balance")
    private long sportsBalance;  // 스포츠머니
    @Column(name = "point")
    private long point;          // 포인트
    @Column(name = "casino_balance")
    private long casinoBalance;  // 카지노머니

    @Column(name = "charged_count")
    private long chargedCount;             // 충전 횟수 누적 합계
    @Column(name = "today_charged_count")
    private long todayChargedCount = 0;    // 24시간 기준 충전 횟수. 00시에 0으로 초기화
    @Column(name = "exchanged_count")
    private long exchangedCount;           // 환전 횟수 누적 합계

    @Column(name = "last_recharged_at")
    private LocalDateTime lastRechargedAt;     // 마지막 충전일
    @Column(name = "processed_at")
    private LocalDateTime processedAt;         // 자동 충전 처리 시간 (wallet Update 등)
    @Column(name = "exchange_processed_at")
    private LocalDateTime exchangeProcessedAt; // 마지막 환전일

    @Column(name = "deposit_total", columnDefinition = "BIGINT default 0")
    private long depositTotal;    // 총 입금액
    @Column(name = "withdraw_total", columnDefinition = "BIGINT default 0")
    private long withdrawTotal;   // 총 출금액
    @Column(name = "total_settlement", columnDefinition = "BIGINT default 0")
    private long totalSettlement; // 총 정산액 (depositTotal - balance & withdrawTotal = totalSettlement)

    @Column(name = "amazon_mileage")
    private long amazonMileage;         // 마일리지
    @Column(name = "today_deposit")
    private long todayDeposit;          // 금일 입금
    @Column(name = "today_withdraw")
    private long todayWithdraw;         // 금일 출금
    @Column(name = "total_amazon_deposit")
    private long totalAmazonDeposit;    // 총판페이지 총 입금액
    @Column(name = "total_amazon_withdraw")
    private long totalAmazonWithdraw;   // 총판페이지 총 출금액
    @Column(name = "total_amazon_settlement")
    private long totalAmazonSettlement; // 총판페이지 총손익 = 총입금 - 총출금

    @Column(name = "amazon_money")
    private long amazonMoney; // 총판페이지에서의 머니
    @Column(name = "amazon_point")
    private long amazonPoint; // 총판페이지에서의 포인트
    @Column(name = "amazon_bonus")
    private long amazonBonus; // 레벨별 보너스

    @Column(name = "has_received_first_deposit_bonus")
    private boolean hasReceivedFirstDepositBonus; // 첫 입금 보너스 수령 여부
    @Column(name = "has_received_daily_bonus")
    private boolean hasReceivedDailyBonus;        // 일일 보너스 수령 여부

    @Column(name = "accumulated_casino_bet", columnDefinition = "BIGINT default 0")
    private long accumulatedCasinoBet; // 누적 카지노 베팅금액
    @Column(name = "accumulated_slot_bet", columnDefinition = "BIGINT default 0")
    private long accumulatedSlotBet;   // 누적 슬롯 베팅금액
    @Column(name = "accumulated_sports_bet", columnDefinition = "BIGINT default 0")
    private long accumulatedSportsBet; // 누적 스포츠 베팅금액

    @Column(name = "today_points", columnDefinition = "BIGINT default 0")
    private long todayPoints; // 오늘 받은 포인트

    @JsonIgnore  // 순환 참조로 인해 설정
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // 첫 입금 보너스 수령 여부 확인
    public boolean hasReceivedFirstDepositBonus() {
        return hasReceivedFirstDepositBonus;
    }

    public void setReceivedFirstDepositBonus(boolean received) {
        this.hasReceivedFirstDepositBonus = received;
    }

    // 일일 보너스 수령 상태 변경
    public void setReceivedDailyBonus(boolean received) {
        this.hasReceivedDailyBonus = received;
    }

    public boolean hasReceivedDailyBonus() {
        return hasReceivedDailyBonus;
    }

}
