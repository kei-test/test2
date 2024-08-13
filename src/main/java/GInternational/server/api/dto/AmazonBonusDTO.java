package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AmazonBonusDTO {

    private double firstRechargeRate; // 가입 첫 입금 보너스 (%)
    private Map<Integer, Double> dailyFirstRechargeRate; // 레벨별 일 첫 입금 보너스 (%)
    private Map<Integer, Double> rechargeRate; // 레벨별 입금 보너스 (%)
    private Map<Integer, Long> dailyRechargeCap; // 레벨별 입금 보너스 일 지급 상한 (금액)
}
