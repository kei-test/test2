package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LevelUpTransactionEnum {

    WAITING("접수"),
    APPROVAL("승인"),
    CANCELLATION("거부");

    private String value;
}
