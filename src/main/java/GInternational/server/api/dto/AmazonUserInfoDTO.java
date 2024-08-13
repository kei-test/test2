package GInternational.server.api.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AmazonUserInfoDTO {

    private Long userId;
    private String username;
    private String nickname;
    private long amazonMoney;
    private long amazonPoint;
    private long todayDeposit;
    private long todayWithdraw;
    private long totalAmazonDeposit;
    private long totalAmazonWithdraw;
    private long totalProfitLoss; // 총손익 = 총입금 - 총출금
    private LocalDateTime createdAt;
    private LocalDateTime lastVisit;
    private long failVisitCount;
}
