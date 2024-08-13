package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LevelBonusPointSettingReqDTO {

    private Long id;
    private int lv;
    private double firstRecharge; // 00:00~23:59 기준 첫충전
    private double todayRecharge; // 첫충전 이후의 추가충전
    private boolean bonusActive;

    private double lossAmount; // 낙첨금
    private double referrerLossAmount; // 추천인낙첨금
}
