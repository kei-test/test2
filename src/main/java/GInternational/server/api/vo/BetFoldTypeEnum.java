package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BetFoldTypeEnum {

    // BetHistory의 "폴더별 보너스" 구분을 위한 이넘값
    SINGLE_FOLDER("단폴"),    // 단폴 (베팅한 게임 수 1, 2개)
    THREE_FOLDER("3폴"),     // 3폴 이상, 5폴 미만 (베팅한 게임수 3, 4개)
    FIVE_FOLDER("5폴"),      // 5폴 이상, 7폴 미만 (베팅한 게임수 5, 6개)
    SEVEN_FOLDER("7폴");     // 7폴 이상 (베팅한 게임수 7개 이상)

    private String value;

    public static BetFoldTypeEnum fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (BetFoldTypeEnum enumValue : BetFoldTypeEnum.values()) {
            if (enumValue.name().equalsIgnoreCase(value) || enumValue.getValue().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }

    public static BetFoldTypeEnum determineType(long betCount) {
        if (betCount >= 1 && betCount <= 2) {
            return SINGLE_FOLDER;
        } else if (betCount >= 3 && betCount <= 4) {
            return THREE_FOLDER;
        } else if (betCount >= 5 && betCount <= 6) {
            return FIVE_FOLDER;
        } else {
            return SEVEN_FOLDER;
        }
    }
}
