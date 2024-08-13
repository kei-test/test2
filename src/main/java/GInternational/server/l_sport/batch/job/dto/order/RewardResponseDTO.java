package GInternational.server.l_sport.batch.job.dto.order;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class RewardResponseDTO {


    private String betType;
    private String startDate;
    private String sportsName;
    private String leagueName;
    private String homeTeam;
    private String homeScore;
    private String awayTeam;
    private String awayScore;
    private String result;
    private String totalReward;
    private LocalDateTime processedAt;
    private String processManager;

    @QueryProjection
    public RewardResponseDTO(String betType, String startDate, String sportsName, String leagueName, String homeTeam, String homeScore, String awayTeam, String awayScore, String result, String totalReward, LocalDateTime processedAt, String processManager) {
        this.betType = betType;
        this.startDate = startDate;
        this.sportsName = sportsName;
        this.leagueName = leagueName;
        this.homeTeam = homeTeam;
        this.homeScore = homeScore;
        this.awayTeam = awayTeam;
        this.awayScore = awayScore;
        this.result = result;
        this.totalReward = totalReward;
        this.processedAt = processedAt;
        this.processManager = processManager;
    }
}
