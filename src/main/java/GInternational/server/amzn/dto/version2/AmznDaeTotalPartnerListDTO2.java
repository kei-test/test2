package GInternational.server.amzn.dto.version2;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AmznDaeTotalPartnerListDTO2 {
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
    private int recommendedCount;
    private String rate;
    private LocalDateTime createdAt;
    private Long num;
    private List<AmznBonPartnerResDTO2> bonList = new ArrayList<>();


}
