package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BetTypeEnum {

    MINI_GAME("미니게임"),
    GA_SANG("가상"),
    IN_PLAY("인플레이"),
    PRE_MATCH("프리매치"),
    CROSS("크로스"),
    HANDICAP("핸디캡"),
    W_D_L("승무패"),
    SPECIAL_ONE("스페셜1"),
    SPECIAL_TWO("스페셜2");

    private String value;

    public static BetTypeEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (BetTypeEnum enumValue : BetTypeEnum.values()) {
            if (enumValue.name().equalsIgnoreCase(value) || enumValue.getValue().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }
}
