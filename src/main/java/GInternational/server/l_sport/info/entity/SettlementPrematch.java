package GInternational.server.l_sport.info.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Index;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "api_settlement_prematch", indexes = {
        @Index(name = "match_id", columnList = "match_id"),
        @Index(name = "bet_idx", columnList = "bet_idx")})
public class SettlementPrematch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_idx")
    private Long sIdx;

    @Column(name = "match_id", nullable = false)
    private String matchId;

    @Column(name = "market_id")
    private String marketId;

    @Column(name = "bet_idx", nullable = false)
    private String betIdx;

    @Column(name = "settlement")
    private String settlement;

    @Column(name = "bookmaker")
    private String bookmaker;

    @Column(name = "last_price", columnDefinition = "varchar(255) default '0'")
    private String lastPrice;

    @Column(name = "last_update")
    private String lastUpdate;

    @Column(name = "is_update", columnDefinition = "char(1) default 'N'")
    private String isUpdate; //배치 처리 시 조건절 파라미터로 사용

    @Column(name = "is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String isModified;  //수동 경기결과 변경 후 배치 처리 시 조건절 파라미터로 사용

    public SettlementPrematch(String betIdx) {
        this.betIdx = betIdx;
    }
}
