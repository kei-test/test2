package GInternational.server.api.dto;


import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.entity.User;
import GInternational.server.api.vo.BetFoldTypeEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class BetHistoryResDTO {

    private Long id;

    private String matchId; // 경기 ID

    private Long betGroupId;

    private String sportName; // 종목명

    private String locationName; // 지역명

    private String leagueName; // 리그명

    private String matchStatus; // 경기상태 구분 // "1" : 경기 시작 전 / "2" : 경기 중 / "3" : 경기종료

    private String homeName; // 홈팀
    private String homeScore; // 홈팀 스코어

    private String awayName; // 원정팀
    private String awayScore; // 원정팀 스코어

    private String idx; // 베팅 id
    private String bet; // 베팅금
    private String betTeam; // 베팅한 팀
    private String betReward; // 당첨금

    private BetFoldTypeEnum betFoldType; // 단폴, 3폴, 5폴, 7폴
    private BetTypeEnum betType; // 승무패, 언오버, 핸디캡, 스페셜1, 스페셜2
    private OrderStatusEnum orderStatus; // 진행, 대기, 적특, 적중, 낙첨


    private String marketName; // 배당명
    private String price;  // 배당률
    private String winRate;
    private String drawRate;
    private String loseRate;

    private String startDate; // 베팅 시작 시간
    private LocalDateTime betStartTime;  //베팅 시간

    private Long userId; // 유저 ID
    private String username; // 유저 이름
    private String nickname; // 유저 별명

    private int limitByLv; // 레벨별 한도
    private String betIp; // 베팅시점의 IP 주소

    private Long sportsBalance;  //유저 보유금액

    private UserMonitoringStatusEnum monitoringStatus; // 주시베팅, 초과베팅

    public BetHistoryResDTO(BetHistory betHistory, String homeScore, String awayScore) {
        this.id = betHistory.getId();
        this.matchId = betHistory.getMatchId();
        this.betGroupId = betHistory.getBetGroupId();
        this.sportName = betHistory.getSportName();
        this.locationName = betHistory.getLocationName();
        this.leagueName = betHistory.getLeagueName();
        this.matchStatus = betHistory.getMatchStatus();
        this.homeName = betHistory.getHomeName();
        this.homeScore = homeScore;
        this.awayName = betHistory.getAwayName();
        this.awayScore = awayScore;
        this.idx = betHistory.getIdx();
        this.bet = betHistory.getBet();
        this.betTeam = betHistory.getBetTeam();
        this.betReward = betHistory.getBetReward();
        this.betFoldType = betHistory.getBetFoldType();
        this.betType = betHistory.getBetType();
        this.orderStatus = betHistory.getOrderStatus();
        this.marketName = betHistory.getMarketName();
        this.price = betHistory.getPrice();
        this.winRate = betHistory.getWinRate();
        this.drawRate = betHistory.getDrawRate();
        this.loseRate = betHistory.getLoseRate();
        this.startDate = betHistory.getStartDate();
        this.betStartTime = betHistory.getBetStartTime();
        this.userId = betHistory.getUser().getId();
        this.username = betHistory.getUser().getUsername();
        this.nickname = betHistory.getUser().getNickname();
        this.limitByLv = betHistory.getLimitByLv();
        this.betIp = betHistory.getBetIp();
        this.sportsBalance = betHistory.getUser().getWallet().getSportsBalance();
        this.monitoringStatus = betHistory.getUser().getMonitoringStatus();
    }
}
