package GInternational.server.api.entity;

import GInternational.server.api.vo.ManagementAccountEnum;
import GInternational.server.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "difference_statistic_account")
public class DifferenceStatisticAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "difference_statistic_account_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "difference_statistic_id")
    private DifferenceStatistic differenceStatistic;

    private String turn; // 순번
    @Enumerated(EnumType.STRING)
    @Column(name = "`usage`")
    private ManagementAccountEnum usage; // 용도 "카지노 캐시, 예비, 앞방, 중간방, 뒷방, 현금"
    @Column(name = "owner_name")
    private String ownerName;
    private Long number;
    private String source;
    private Boolean isUse;
    @Column(name = "transfer_limit")
    private String transferLimit;
    @Column(name = "current_money")
    private Long currentMoney;
    private String memo;
}
