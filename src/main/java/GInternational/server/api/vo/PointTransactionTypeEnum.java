package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointTransactionTypeEnum {

    적립("적립"),
    차감("차감");



    private String value;


}
