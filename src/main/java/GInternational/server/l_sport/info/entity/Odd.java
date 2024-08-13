package GInternational.server.l_sport.info.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity(name = "api_odds")
@Table(name = "api_odds", indexes = {
        @Index(name = "idx", columnList = "idx"),
        @Index(name = "market_id", columnList = "market_id"),
        @Index(name = "match_id", columnList = "match_id"),
        @Index(name = "bet_status", columnList = "bet_status")})
public class Odd {

    @Id
    @Column(name = "idx",length = 50,nullable = false)
    private String idx;

    @Column(name = "match_id",length = 50,nullable = false)
    private String matchId;

    @Column(name = "market_id",length = 50,nullable = false)
    private String marketId;

    @Column(name = "market_name")
    private String marketName;

    @Column(name = "market_name_ko")
    private String marketNameKo;

    @Column(name = "bookmaker")
    private String bookmaker;

    @Column(name = "bet_name_ko")
    private String betNameKo;

    @Column(name = "bet_name")
    private String betName;

    @Column(name = "bet_status",length = 50)
    private String betStatus;

//    @Column(name = "bet_status_admin",columnDefinition = "varchar(50) default '0'")
//    private String betStatusAdmin;
//
//    @Column(name = "bet_status_bico",columnDefinition = "varchar(50) default '0'")
//    private String betStatusBico;
//
//    @Column(name = "bet_status_fss",columnDefinition = "varchar(50) default '0'")
//    private String betStatusFss;

    @Column(name = "start_price",columnDefinition = "varchar(50) default '0'")
    private String startPrice;

    @Column(name = "price",length = 50,nullable = false,columnDefinition = "varchar(50) default '0'")
    private String price;

//    @Column(name = "price_admin",length = 50,nullable = false,columnDefinition = "varchar(50) default '0'")
//    private String priceAdmin;
//
//    @Column(name = "price_bico",length = 50,nullable = false,columnDefinition = "varchar(50) default '0'")
//    private String priceBico;
//
//    @Column(name = "price_fss",length = 50,nullable = false,columnDefinition = "varchar(50) default '0'")
//    private String priceFss;

    @Column(name = "lsports_line")
    private String lsportsLine;

    @Column(name = "lsports_base")
    private String lsportsBase;

    @Column(name = "line",columnDefinition = "varchar(50) default '0'")
    private String line;

    @Column(name = "base_line",columnDefinition = "varchar(50) default '0'")
    private String baseLine;

    @Column(name = "settlement")
    private String settlement;

    @Column(name = "last_update")
    private String lastUpdate;

    @Column(name = "last_update_ls")
    private String lastUpdateLs;

    @Column(name = "sort",columnDefinition = "varchar(50) default '0'")
    private String sort;

    @Column(name = "api", columnDefinition = "char(1) default 'N'")
    private char api;   //관리자의 배당률 수동 변경 기능이 존재하여 스냅샷에서 받는 업데이트 정보인지, 관리자가 수동 변경한 정보인지 판별하는 필드

    @Column(name = "is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String isModified;

    @Column(name = "rain_is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String rainIsModified;

    @Column(name = "mega_is_modified", nullable = false, columnDefinition = "char(1) default 'N'")
    private String megaIsModified;


    public Odd(String idx,String settlement) {
        this.idx = idx;
        this.settlement = settlement;
    }

    public Odd(String idx) {
        this.idx = idx;
    }
}
