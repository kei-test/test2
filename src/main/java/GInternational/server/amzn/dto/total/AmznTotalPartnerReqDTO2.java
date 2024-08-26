package GInternational.server.amzn.dto.total;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
public class AmznTotalPartnerReqDTO2 {


    //하부 총판 조회
    private Long id;
    private String username;
    private String nickname;
    private long amazonMoney;
    private long amazonPoint;
    private long todayDeposit;          // 금일 입금
    private long todayWithdraw;         // 금일 출금
    private long totalAmazonDeposit;    // 총판페이지 총 입금액
    private long totalAmazonWithdraw;   // 총판페이지 총 출금액
    private long totalAmazonSettlement; // 총판페이지 총손익 = 총입금 - 총출금
    private int recommendedCount;       // 총 하부유저
    private String partnerType;
    private Long daeId;
    private Long bonId;
    private Long buId;
    private Long chongId;
    private LocalDateTime createdAt;


    @QueryProjection
    public AmznTotalPartnerReqDTO2(Long id, String username, String nickname, long amazonMoney, long amazonPoint, long todayDeposit, long todayWithdraw, long totalAmazonDeposit, long totalAmazonWithdraw, long totalAmazonSettlement,
                                   int recommendedCount, String partnerType, Long daeId, Long bonId, Long buId, Long chongId, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.amazonMoney = amazonMoney;
        this.amazonPoint = amazonPoint;
        this.todayDeposit = todayDeposit;
        this.todayWithdraw = todayWithdraw;
        this.totalAmazonDeposit = totalAmazonDeposit;
        this.totalAmazonWithdraw = totalAmazonWithdraw;
        this.totalAmazonSettlement = totalAmazonSettlement;
        this.recommendedCount = recommendedCount;
        this.partnerType = partnerType;
        this.daeId = daeId;
        this.bonId = bonId;
        this.buId = buId;
        this.chongId = chongId;
        this.createdAt = createdAt;
    }
}
