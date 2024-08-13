package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionEnum {

    UNREAD("확인 안함"),
    WAITING("대기"),
    TIMEOUT("시간 초과"),
    APPROVAL("완료"),
    AUTO_APPROVAL("자동충전 완료"),
    CANCELLATION("취소");


    private String value;
}
