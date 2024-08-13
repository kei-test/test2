package GInternational.server.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "amazon_bonus")
public class AmazonBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_recharge_rate", nullable = false)
    private double firstRechargeRate; // 가입 첫 입금 보너스 (%)

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "daily_first_recharge_rate", joinColumns = @JoinColumn(name = "bonus_id"))
    @MapKeyColumn(name = "lv")
    @Column(name = "percentage")
    private Map<Integer, Double> dailyFirstRechargeRate; // 레벨별 일 첫 입금 보너스 (%)

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "recharge_rate", joinColumns = @JoinColumn(name = "bonus_id"))
    @MapKeyColumn(name = "lv")
    @Column(name = "percentage")
    private Map<Integer, Double> rechargeRate; // 레벨별 입금 보너스 (%)

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "daily_recharge_cap", joinColumns = @JoinColumn(name = "bonus_id"))
    @MapKeyColumn(name = "lv")
    @Column(name = "amount")
    private Map<Integer, Long> dailyRechargeCap; // 레벨별 입금 보너스 일 지급 상한 (금액)
}
