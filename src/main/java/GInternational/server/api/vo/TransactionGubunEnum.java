package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TransactionGubunEnum {

    SPORTS("sportsBalance"),
    CASINO("casinoBalance"),
    EXCHANGE("exchange");


    private String value;
}
