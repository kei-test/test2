package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "level_account_setting")
public class LevelAccountSetting {

    /**
     * 어드민 피그마 127번 NEW 레벨설정 중 제일윗부분 충전계좌 안내
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_account_setting_id")
    private Long id;

    private int lv;
    @Column(name = "bank_name")
    private String bankName;      // 은행명
    @Column(name = "account_number")
    private String accountNumber; // 계좌번호
    @Column(name = "owner_name")
    private String ownerName;     // 예금주
    @Column(name = "cs_number")
    private String csNumber;      // 고객센터번호
}
