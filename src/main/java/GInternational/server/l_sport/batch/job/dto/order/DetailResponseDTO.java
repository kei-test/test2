package GInternational.server.l_sport.batch.job.dto.order;


import GInternational.server.api.vo.BetFoldTypeEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DetailResponseDTO {
    private Long userId;
    private String betIp;
    private String betStatus;
    private UserMonitoringStatusEnum monitoringStatus;
    private String readStatus;
    private String readBy;
    private LocalDateTime readAt;
    private String username;
    private String nickname;
    private String matchId;
    private String startDate;
    private String leagueName;
    private String sportName;
    private String homeName;
    private String homeScore;
    private String awayName;
    private String awayScore;
    private String marketName;
    private String winRate;
    private String drawRate;
    private String loseRate;
    private String idx;
    private Long betGroupId;
    private String betTeam;
    private String bet;
    private String price;
    private String settlement;
    private String betReward;
    private String matchStatus;
    private boolean deleted;
    private BetTypeEnum betType;
    private BetFoldTypeEnum betFoldType;
    private OrderStatusEnum orderStatus;
    private LocalDateTime betStartTime;
    private LocalDateTime processedAt;
    private LocalDateTime deletedAt;


    @QueryProjection
    public DetailResponseDTO(Long userId, String betIp,String betStatus, UserMonitoringStatusEnum monitoringStatus, String readStatus, String readBy,LocalDateTime readAt,
                             String username,String nickname, String matchId,String startDate, String leagueName, String sportName,
                             String homeName, String awayName, String marketName, String winRate, String drawRate, String loseRate,
                             String idx, Long betGroupId, String betTeam, String bet, String price, String settlement, String betReward,
                             String matchStatus,boolean deleted, BetTypeEnum betType, BetFoldTypeEnum betFoldType, OrderStatusEnum orderStatus,
                             LocalDateTime betStartTime, LocalDateTime processedAt) {
        this.userId = userId;
        this.betIp = betIp;
        this.betStatus = betStatus;
        this.monitoringStatus = monitoringStatus;
        this.readStatus = readStatus;
        this.readBy = readBy;
        this.readAt = readAt;
        this.username = username;
        this.nickname = nickname;
        this.matchId = matchId;
        this.startDate = startDate;
        this.leagueName = leagueName;
        this.sportName = sportName;
        this.homeName = homeName;
        this.awayName = awayName;
        this.marketName = marketName;
        this.winRate = winRate;
        this.drawRate = drawRate;
        this.loseRate = loseRate;
        this.idx = idx;
        this.betGroupId = betGroupId;
        this.betTeam = betTeam;
        this.bet = bet;
        this.price = price;
        this.settlement = settlement;
        this.betReward = betReward;
        this.matchStatus = matchStatus;
        this.deleted = deleted;
        this.betType = betType;
        this.betFoldType = betFoldType;
        this.orderStatus = orderStatus;
        this.betStartTime = betStartTime;
        this.processedAt = processedAt;
    }
}
