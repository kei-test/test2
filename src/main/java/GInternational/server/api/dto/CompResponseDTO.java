package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompResponseDTO {

    private Long id; // 콤프 결과의 고유 ID
    private Long userId; // 콤프 적립을 신청한 유저의 ID
    private int lv; // 유저의 레벨
    private String username;
    private String nickname;
    private LocalDateTime createdAt; // 콤프 적립을 신청한 시간
    private LocalDateTime processedAt; // 처리 시간
    private BigDecimal lastDayChargeBalance; // 전일 충전한 캐시
    private BigDecimal calculatedReward; // 계산된 적립 포인트 (전일 환전한 카지노 머니의 300%)
    private BigDecimal rate; // 지급 퍼센트
    private BigDecimal lastDayAmount; // 전날 베팅 금액(롤링금액)
    private long sportsBalance; // 보유 잔고
    private long casinoBalance; // 보유 카지노잔고
    private String status; // 승인 상태의 문자열 표현
    private String userIp;
}
