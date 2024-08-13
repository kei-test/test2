package GInternational.server.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "exp_setting")
public class ExpSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exp_setting_id")
    private Long id;

    @Column(name = "min_exp")
    private long minExp;
    @Column(name = "max_exp")
    private long maxExp;
    private int lv;
}
