package GInternational.server.api.dto;

import GInternational.server.api.vo.CouponTransactionEnum;
import GInternational.server.api.vo.CouponTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponResponseDTO {

    private Long id;
    private String couponName; // 쿠폰이름
    private CouponTypeEnum couponTypeEnum; // 쿠폰타입
    private int sportsBalance; // 쿠폰 금액
    private int point; // 포인트 금액
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime lastModifiedAt; // 처리 시간
    private LocalDateTime expirationDateTime; // 유효 기간
    private CouponTransactionEnum status; // 처리 현황
    private String site;
    private String memo;
}
