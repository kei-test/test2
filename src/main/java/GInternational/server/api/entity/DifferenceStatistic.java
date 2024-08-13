package GInternational.server.api.entity;

import GInternational.server.api.vo.ManagementAccountEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "difference_statistic")
public class DifferenceStatistic {

    /**
     * "차액통계 목록"
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "difference_statistic_id")
    private Long id;

    @Column(name = "current_sports_balance")
    private long currentSportsBalance; // "보유캐시"
    @Column(name = "current_point")
    private long currentPoint;         // "보유포인트"
    @Column(name = "total_bet")
    private long totalBet;             // "베팅금"
    @Column(name = "front_account")
    private long frontAccount;         // "앞방"
    @Column(name = "middle_account")
    private long middleAccount;        // "중간방"
    @Column(name = "back_account")
    private long backAccount;          // "뒷방"
    @Column(name = "total_account")
    private long totalAccount;         // "앞,중,뒷방 합산금액"
    @Column(name = "bigo")
    private String bigo;               // "비고"
    @Column(name = "operating_expense")
    private long operatingExpense;     // "운영비"
    @Column(name = "won_exchange")
    private long wonExchange;          // "원환전"
    @Column(name = "commission")
    private long commission;           // "수수료" // 원환전 x 수수료율% = 수수료(계산된 금액)
    @Column(name = "dong_exchange")
    private long dongExchange;         // "동환전"
    @Column(name = "user_count")
    private long userCount;            // "회원수"
    @Column(name = "total_recharge")
    private long totalRecharge;        // "충전"
    @Column(name = "total_exchange")
    private long totalExchange;        // "환전"
    @Column(name = "subtract")
    private long subtract;             // "충-환"
    @Column(name = "difference")
    private long difference;           // "차액"
    @Column(name = "created_at")
    private LocalDateTime createdAt;   // 생성시간

    @JsonIgnore
    @OneToMany(mappedBy = "differenceStatistic", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DifferenceStatisticAccount> accounts = new ArrayList<>();
}
