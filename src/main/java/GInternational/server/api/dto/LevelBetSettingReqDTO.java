package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LevelBetSettingReqDTO {

    private Integer lv; // 레벨 (1~10)

    /**
     *  스포츠 부분
     */
    private Integer minBetWdl; // 최소베팅액(승무패)
    private Integer maxBetWdl; // 최대베팅액(승무패)
    private Integer limitWdl;  // 상한가(승무패)

    private Integer minBetWdlSingle; // 최소베팅액(승무패-단폴)
    private Integer maxBetWdlSingle; // 최대베팅액(승무패-단폴)
    private Integer limitWdlSingle;  // 상한가(승무패-단폴)

    private Integer minBetCross; // 최소베팅액(크로스)
    private Integer maxBetCross; // 최대베팅액(크로스)
    private Integer limitCross;  // 상한가(크로스)

    private Integer minBetCrossSingle; // 최소베팅액(크로스-단폴)
    private Integer maxBetCrossSingle; // 최대베팅액(크로스-단폴)
    private Integer limitCrossSingle;  // 상한가(크로스-단폴)

    private Integer minBetHandicap; // 최소베팅액(핸디캡)
    private Integer maxBetHandicap; // 최대베팅액(핸디캡)
    private Integer limitHandicap;  // 상한가(핸디캡)

    private Integer minBetHandicapSingle; // 최소베팅액(핸디캡-단폴)
    private Integer maxBetHandicapSingle; // 최대베팅액(핸디캡-단폴)
    private Integer limitHandicapSingle;  // 상한가(핸디캡-단폴)

    private Integer minBetSpecial; // 최소베팅액(스페셜)
    private Integer maxBetSpecial; // 최대베팅액(스페셜)
    private Integer limitSpecial;  // 상한가(스페셜)

    private Integer minBetSpecialSingle; // 최소베팅액(스페셜-단폴)
    private Integer maxBetSpecialSingle; // 최대베팅액(스페셜-단폴)
    private Integer limitSpecialSingle;  // 상한가(스페셜-단폴)

    private Integer minBetSpecial2; // 최소베팅액(스페셜2)
    private Integer maxBetSpecial2; // 최대베팅액(스페셜2)
    private Integer limitSpecial2;  // 상한가(스페셜2)

    private Integer minBetSpecial2Single; // 최소베팅액(스페셜2-단폴)
    private Integer maxBetSpecial2Single; // 최대베팅액(스페셜2-단폴)
    private Integer limitSpecial2Single;  // 상한가(스페셜2-단폴)

    /**
     *  미니게임 부분
     */
    // 아직 미니게임 없음 (2024-07-23)
}
