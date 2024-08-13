package GInternational.server.l_sport.batch.job.dto.edit;

import GInternational.server.api.vo.BetTypeEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class EditMatchResultDTO {

    private String matchId;  //경기 id
    private String startDate; //게임시간
    private String sportsName;  //종목명
    private String locationName;  //지역명
    private String leagueName;  //리그명
    private String homeName;  //홈팀
    private String awayName;  //원정팀
    private String homeScore;  //홈팀 점수
    private String awayScore;  //원정팀 점수
    private String status;  //경기 상태
    private String marketName;  //마켓명
    private String idx;
    private String price;
    private String baseLine;
    private String betName;
    private String settlement;


    @QueryProjection
    public EditMatchResultDTO(String matchId, String startDate, String sportsName, String locationName, String leagueName, String homeName, String awayName, String homeScore, String awayScore, String status, String marketName, String idx, String price, String baseLine, String betName, String settlement) {
        this.matchId = matchId;
        this.startDate = startDate;
        this.sportsName = sportsName;
        this.locationName = locationName;
        this.leagueName = leagueName;
        this.homeName = homeName;
        this.awayName = awayName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.status = status;
        this.marketName = marketName;
        this.idx = idx;
        this.price = price;
        this.baseLine = baseLine;
        this.betName = betName;
        this.settlement = settlement;
    }
}
