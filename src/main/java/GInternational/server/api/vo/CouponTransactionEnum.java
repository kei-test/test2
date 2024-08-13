package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponTransactionEnum {

    APPROVAL("완료"),
    CANCELLATION("취소"),
    WAITING("대기"),
    EXPIRED("유효기간만료");

    private String value;
}
