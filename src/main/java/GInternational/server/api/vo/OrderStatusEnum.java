package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    WAITING("대기"),       // 대기 중인 베팅
    CANCEL_HIT("적중특례"), // 적중 특례로 처리된 베팅 (관리자 취소) 3
    CANCEL("취소"),        // 베팅 취소 (유저) -1
    HIT("적중"),           // 적중된 베팅 2
    FAIL("낙첨");          // 실패한 베팅 1

    private String value;

    public static OrderStatusEnum fromValue(String value) {
        for (OrderStatusEnum enumValue : OrderStatusEnum.values()) {
            if (enumValue.getValue().equals(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }
}
