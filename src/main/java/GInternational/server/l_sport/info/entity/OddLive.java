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
@Entity(name = "api_odds_live")
@Table(name = "api_odds_live", indexes = {
        @Index(name = "idx", columnList = "idx"),
        @Index(name = "match_id", columnList = "match_id"),
        @Index(name = "market_id", columnList = "market_id"),
        @Index(name = "bet_status", columnList = "bet_status")})
public class OddLive {

    @Id
    @Column(name = "idx", nullable = false, columnDefinition = "varchar(50)")
    private String idx;

    @Column(name = "match_id", nullable = false, columnDefinition = "varchar(50)")
    private String matchId;

    @Column(name = "market_id", nullable = false, columnDefinition = "varchar(50)")
    private String marketId;

    @Column(name = "market_name")
    private String marketName;

    @Column(name = "market_name_ko")
    private String marketNameKo;

    @Column(name = "bookmaker")
    private String bookmaker;

    @Column(name = "bet_name_ko")
    private String betNameko;

    @Column(name = "bet_name")
    private String betName;

    @Column(name = "bet_status", columnDefinition = "varchar(50)")
    private String betStatus;

    @Column(name = "start_price", columnDefinition = "varchar(50) default '0'")
    private String startPrice;

    @Column(name = "price", nullable = false, columnDefinition = "varchar(50) default '0'")
    private String price;

    @Column(name = "lsports_line", columnDefinition = "varchar(50)")
    private String lsportsLine;

    @Column(name = "lsports_base", columnDefinition = "varchar(50)")
    private String lsportsBase;

    @Column(name = "line", columnDefinition = "varchar(50) default '0'")
    private String line;

    @Column(name = "base_line", columnDefinition = "varchar(50) default '0'")
    private String baseLine;

    @Column(name = "settlement", columnDefinition = "varchar(50)")
    private String settlement;

    @Column(name = "first_update", columnDefinition = "datetime default CURRENT_TIMESTAMP", updatable = false, insertable = false)
    private String firstUpdate;

    @Column(name = "open_update", columnDefinition = "varchar(50)")
    private String openUpdate;

    @Column(name = "close_update", columnDefinition = "varchar(50)")
    private String closeUpdate;

    @Column(name = "last_update", columnDefinition = "varchar(50)")
    private String lastUpdate;

    @Column(name = "sort", columnDefinition = "varchar(50) default '0'")
    private String sort;

    @Column(name = "is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String isModified;

    @Column(name = "rain_is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String rainIsModified;

    @Column(name = "mega_is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String megaIsModified;


    public OddLive(String idx,String settlement) {
        this.idx = idx;
        this.settlement = settlement;
    }


    public OddLive(String idx) {
        this.idx = idx;
    }
}
