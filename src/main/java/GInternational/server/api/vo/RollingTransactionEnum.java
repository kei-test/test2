package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RollingTransactionEnum {

    APPROVAL("완료");

    private String value;
}
