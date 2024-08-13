package GInternational.server.api.vo;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BetFoldCountEnum {

    // 전체 베팅목록의 "폴더"수 표기를 위한 이넘값
    ONE("1"),
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("10"),
    ELEVEN("11"),
    TWELVE("12"),
    THIRTEEN("13"),
    FOURTEEN("14"),
    FIFTEEN("15");

    private String value;

    public static BetFoldCountEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (BetFoldCountEnum enumValue : BetFoldCountEnum.values()) {
            if (enumValue.name().equalsIgnoreCase(value) || enumValue.getValue().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }

    public static BetFoldCountEnum fromBetCount(long betCount) {
        switch ((int) betCount) {
            case 1:
                return BetFoldCountEnum.ONE;
            case 2:
                return BetFoldCountEnum.TWO;
            case 3:
                return BetFoldCountEnum.THREE;
            case 4:
                return BetFoldCountEnum.FOUR;
            case 5:
                return BetFoldCountEnum.FIVE;
            case 6:
                return BetFoldCountEnum.SIX;
            case 7:
                return BetFoldCountEnum.SEVEN;
            case 8:
                return BetFoldCountEnum.EIGHT;
            case 9:
                return BetFoldCountEnum.NINE;
            case 10:
                return BetFoldCountEnum.TEN;
            case 11:
                return BetFoldCountEnum.ELEVEN;
            case 12:
                return BetFoldCountEnum.TWELVE;
            case 13:
                return BetFoldCountEnum.THIRTEEN;
            case 14:
                return BetFoldCountEnum.FOURTEEN;
            default:
                return BetFoldCountEnum.FIFTEEN;
        }
    }
}
