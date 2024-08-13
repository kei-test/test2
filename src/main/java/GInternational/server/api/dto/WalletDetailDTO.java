package GInternational.server.api.dto;

import GInternational.server.api.entity.Wallet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
public class WalletDetailDTO {
    private Long id;

    private String ownerName;    // 예금주
    private long number;         // 계좌 번호
    private String bankName;     // 은행명
    private String bankPassword; // 환전 비밀번호

    private long sportsBalance;  // 스포츠머니
    private long casinoBalance;  // 카지노머니
    private long point;          // 포인트

    private long amazonMoney;    // 총판페이지 머니
    private long amazonPoint;    // 총판페이지 포인트
    private long amazonBonus;    // 총판페이지 레벨별 보너스
    private long amazonMileage;  // 총판페이지 마일리지

    private long depositTotal;   // 기간별 입금액
    private long withdrawTotal;  // 기간별 출금액
    private long totalSettlement;// 기간별 정산액

    private long todayDeposit;          // 금일 입금액
    private long todayWithdraw;         // 금일 출금액
    private long totalAmazonDeposit;    // 총판페이지 총 입금액
    private long totalAmazonWithdraw;   // 총판페이지 총 출금액
    private long totalAmazonSettlement; // 총판페이지 총손익 = 총입금 - 총출금

    private long chargedCount;   // 충전횟수
    private long exchangedCount; // 환전횟수

    private LocalDateTime lastRechargedAt;     // 마지막 충전일
    private LocalDateTime processedAt;         // 처리 시간 (wallet Update 등)
    private LocalDateTime exchangeProcessedAt; // 마지막 환전일


    public WalletDetailDTO(Wallet wallet) {
        this.id = wallet.getId();

        this.ownerName = Optional.ofNullable(wallet.getOwnerName()).orElse("");
        this.number = Optional.ofNullable(wallet.getNumber()).orElse(0L);

        this.bankName = Optional.ofNullable(wallet.getBankName()).orElse("");
        this.bankPassword = Optional.ofNullable(wallet.getBankPassword()).orElse("");

        this.sportsBalance = wallet.getSportsBalance();
        this.casinoBalance = wallet.getCasinoBalance();
        this.point = wallet.getPoint();

        this.amazonMoney = wallet.getAmazonMoney();
        this.amazonPoint = wallet.getAmazonPoint();
        this.amazonBonus = wallet.getAmazonBonus();
        this.amazonMileage = wallet.getAmazonMileage();

        this.depositTotal = wallet.getDepositTotal();
        this.withdrawTotal = wallet.getWithdrawTotal();
        this.totalSettlement = wallet.getTotalSettlement();

        this.todayDeposit = wallet.getTodayDeposit();
        this.todayWithdraw = wallet.getTodayWithdraw();
        this.totalAmazonDeposit = wallet.getTotalAmazonDeposit();
        this.totalAmazonWithdraw = wallet.getTotalAmazonWithdraw();
        this.totalAmazonSettlement = wallet.getTotalAmazonSettlement();

        this.chargedCount = wallet.getChargedCount();
        this.exchangedCount = wallet.getExchangedCount();

        this.lastRechargedAt = wallet.getLastRechargedAt();
        this.processedAt = wallet.getProcessedAt();
        this.exchangeProcessedAt = wallet.getExchangeProcessedAt();
    }
}
