package GInternational.server.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "level_bonus_point_setting")
public class LevelBonusPointSetting {

    /**
     * 어드민 피그마 127번 NEW 레벨설정 중 하루첫 충전시(%), 매충전 포인트(%) 2줄만
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_bonus_point_setting_id")
    private Long id;

    private int lv;
    @Column(name = "first_recharge")
    private double firstRecharge; // 00:00~23:59 기준 첫충전
    @Column(name = "today_recharge")
    private double todayRecharge; // 첫충전 이후의 추가충전

    @Column(name = "bonus_active", nullable = false)
    private boolean bonusActive = true;

    @Column(name = "loss_amount")
    private double lossAmount; // 낙첨금

    @Column(name = "referrer_loss_amount")
    private double referrerLossAmount; // 추천인낙첨금
}