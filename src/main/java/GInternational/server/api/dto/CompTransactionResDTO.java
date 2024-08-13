package GInternational.server.api.dto;

import GInternational.server.api.vo.RollingTransactionEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompTransactionResDTO {
    private Long id;
    private Long userId;
    private int lv;
    private String username;
    private String nickname;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    private BigDecimal lastDayChargeBalance; // 전일 충전한 캐시
    private BigDecimal calculatedReward; // 계산된 적립 포인트 (전일 환전한 카지노 머니의 300%)
    private BigDecimal rate; // 지급 퍼센트
    private BigDecimal lastDayAmount; // 전날 베팅 금액(롤링 금액)
    private long sportsBalance;  // 스포츠 머니
    private long casinoBalance;  // 카지노 머니
    private RollingTransactionEnum status;
    private String userIp;
}
