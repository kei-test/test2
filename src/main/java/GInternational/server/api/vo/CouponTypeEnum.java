package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponTypeEnum {

    머니쿠폰("머니쿠폰"),
    행운복권("행운복권");

    private String value;
}
