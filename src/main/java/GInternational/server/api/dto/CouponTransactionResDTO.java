package GInternational.server.api.dto;

import GInternational.server.api.vo.CouponTypeEnum;
import GInternational.server.api.vo.CouponTransactionEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CouponTransactionResDTO {
    private Long id;
    private Long userId;
    private String couponName; // 쿠폰명
    private CouponTypeEnum couponTypeEnum; // 쿠폰타입 (머니쿠폰, 행운복권)
    private String username; // 회원가입시 입력한 아이디
    private String nickname; // 회원가입시 입력한 닉네임

    private long sportsBalance; // 지급할 캐시
    private long point; // 지급할 포인트
    private LocalDateTime expirationDateTime; // 머니쿠폰 유효기간

    private CouponTransactionEnum status;

    private String site;
    private String memo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastModifiedAt;
}
