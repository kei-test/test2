package GInternational.server.api.dto;

import GInternational.server.api.vo.CouponTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponRequestDTO {

    @NotNull
    private String username; // 회원가입시 입력한 아이디

    private Long sportsBalance; // 쿠폰에 포함될 캐시
    private Long point; // 쿠폰에 포함될 포인트
    private String couponName; // 쿠폰이름
    private CouponTypeEnum couponTypeEnum; // 쿠폰타입 (머니쿠폰, 행운복권)

    private int quantity; // 발급 수량

    private String memo; // 메모

    @NotNull
    private LocalDateTime expirationDateTime; // 머니쿠폰 유효기간
}
