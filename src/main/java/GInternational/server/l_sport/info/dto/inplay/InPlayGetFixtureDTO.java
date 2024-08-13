package GInternational.server.l_sport.info.dto.inplay;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@Setter
@Getter
public class InPlayGetFixtureDTO {
    //[game]
    private String matchId;
    private String leagueName;
    private String locationName;
    private String sportsName;
    private String status;
    private String homeName;
    private String awayName;
    private String startDate;
    private String homeScore;
    private String awayScore;
    //[odd]
    private String marketId;
    private String marketName;
    private String idx;
    private String betName;
    private String line;
    private String baseLine;
    private String price;
    private String startPrice;
    private String lastUpdate;
    private String betStatus;
    private String period; //현재 경기의 period status value
    private String period1;
    private String period1Home;
    private String period1Away;
    private String period2;
    private String period2Home;
    private String period2Away;
    private String period3;
    private String period3Home;
    private String period3Away;
    private String period4;
    private String period4Home;
    private String period4Away;
    private String period5;
    private String period5Home;
    private String period5Away;
    private String period6;
    private String period6Home;
    private String period6Away;
    private String period7;
    private String period7Home;
    private String period7Away;
    private String period8;
    private String period8Home;
    private String period8Away;
    private String period9;
    private String period9Home;
    private String period9Away;
    private String period10;
    private String period10Home;
    private String period10Away;

    //[period]
//    private Long periodType;
//    private Boolean isFinished;
//    private Boolean isConfirmed;
//    private String periodHomeResultPosition;
//    private String periodHomeResultValue;
//    private String periodAwayResultPosition;
//    private String periodAwayResultValue;
//    private Timestamp fixtureStartDate;

//    private Boolean display;

    @QueryProjection
    public InPlayGetFixtureDTO(String matchId, String leagueName, String locationName, String sportsName, String status, String homeName, String awayName, String startDate, String homeScore, String awayScore, String marketId, String marketName, String idx, String betName, String line, String baseLine, String price, String startPrice, String lastUpdate, String betStatus, String period, String period1, String period1Home, String period1Away, String period2, String period2Home, String period2Away, String period3, String period3Home, String period3Away, String period4, String period4Home, String period4Away, String period5, String period5Home, String period5Away, String period6, String period6Home, String period6Away, String period7, String period7Home, String period7Away, String period8, String period8Home, String period8Away, String period9, String period9Home, String period9Away, String period10, String period10Home, String period10Away) {
        this.matchId = matchId;
        this.leagueName = leagueName;
        this.locationName = locationName;
        this.sportsName = sportsName;
        this.status = status;
        this.homeName = homeName;
        this.awayName = awayName;
        this.startDate = startDate;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.marketId = marketId;
        this.marketName = marketName;
        this.idx = idx;
        this.betName = betName;
        this.line = line;
        this.baseLine = baseLine;
        this.price = price;
        this.startPrice = startPrice;
        this.lastUpdate = lastUpdate;
        this.betStatus = betStatus;
        this.period = period;
        this.period1 = period1;
        this.period1Home = period1Home;
        this.period1Away = period1Away;
        this.period2 = period2;
        this.period2Home = period2Home;
        this.period2Away = period2Away;
        this.period3 = period3;
        this.period3Home = period3Home;
        this.period3Away = period3Away;
        this.period4 = period4;
        this.period4Home = period4Home;
        this.period4Away = period4Away;
        this.period5 = period5;
        this.period5Home = period5Home;
        this.period5Away = period5Away;
        this.period6 = period6;
        this.period6Home = period6Home;
        this.period6Away = period6Away;
        this.period7 = period7;
        this.period7Home = period7Home;
        this.period7Away = period7Away;
        this.period8 = period8;
        this.period8Home = period8Home;
        this.period8Away = period8Away;
        this.period9 = period9;
        this.period9Home = period9Home;
        this.period9Away = period9Away;
        this.period10 = period10;
        this.period10Home = period10Home;
        this.period10Away = period10Away;
    }
}