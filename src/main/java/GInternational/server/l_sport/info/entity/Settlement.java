package GInternational.server.l_sport.info.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "api_settlement", indexes = {
        @Index(name = "match_id", columnList = "match_id"),
        @Index(name = "bet_idx", columnList = "bet_idx"),
        @Index(name = "is_update", columnList = "is_update")})
public class Settlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s_idx")
    private Long sIdx;

    @Column(name = "match_id")
    private String matchId;

    @Column(name = "market_id")
    private String marketId;

    @Column(name = "bet_idx")
    private String betIdx;

    @Column(name = "settlement")
    private String settlement;

    @Column(name = "bookmaker")
    private String bookmaker;

    @Column(name = "last_price",columnDefinition = "varchar(255) default '0'")
    private String lastPrice;

    @Column(name = "last_update")
    private String lastUpdate;

    @Column(name = "is_update", nullable = false, columnDefinition = "char(1) default 'N'")
    private String isUpdate;

    @Column(name = "is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String isModified;


    public Settlement(String betIdx) {
        this.betIdx = betIdx;
    }
}
