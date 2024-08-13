package GInternational.server.api.entity;

import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "money_log")
public class MoneyLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "money_log_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String username;
    private String nickname;

    @Column(name = "used_sports_balance")
    private Long usedSportsBalance; // 사용머니

    @Column(name = "final_sports_balance")
    private Long finalSportsBalance; // 최종머니
    private String bigo; // 비고

    private String site; // 사이트 (윈드 메가 등)

    @Enumerated(EnumType.STRING)
    private MoneyLogCategoryEnum category; // 충전, 환전, 베팅 차감, 당첨, 포인트 전환, + 결과 수정


    @Builder
    public MoneyLog(User user, String username, String nickname, Long usedSportsBalance, Long finalSportsBalance, String bigo, String site, MoneyLogCategoryEnum category) {
        this.user = user;
        this.username = username;
        this.nickname = nickname;
        this.usedSportsBalance = usedSportsBalance;
        this.finalSportsBalance = finalSportsBalance;
        this.bigo = bigo;
        this.site = site;
        this.category = category;
    }
}