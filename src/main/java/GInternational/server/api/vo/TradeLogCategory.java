package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeLogCategory {

    MONEY("머니"),
    POINT("포인트");

    private String value;
}
