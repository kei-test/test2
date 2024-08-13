package GInternational.server.api.dto;


import GInternational.server.api.entity.User;
import GInternational.server.api.vo.BetFoldTypeEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
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
public class BetHistoryReqDTO {

    private String matchId; // 경기 ID

    private String sportName; // 종목명

    private String locationName; // 지역명
    private String locationIcon; // 지역 아이콘

    private String leagueName; // 리그명
    private String leagueIcon; // 리그 아이콘

    private String matchStatus; // 경기상태 구분 // "1" : 베팅가능 상태 / "2" : 베팅불가 상태 / "3" : 경기종료 상태

    private String homeName; // 홈팀
    private String homeIcon; // 홈팀 아이콘
    private String awayName; // 원정팀
    private String awayIcon; // 원정팀 아이콘

    private String idx; // 베팅 id
    private String bet; // 베팅금
    private String betTeam; // 베팅한 팀
    private String betReward; // 당첨금

    private BetFoldTypeEnum betFoldType; // 단폴, 3폴, 5폴, 7폴
    private BetTypeEnum betType; // 승무패, 언오버, 핸디캡, 스페셜1, 스페셜2
    private OrderStatusEnum orderStatus; // 진행, 대기, 적특, 적중, 낙첨

    private String marketId; // 배당 id
    private String marketName; // 배당명
    private String price;  // 배당률
    private String winRate;
    private String drawRate;
    private String loseRate;
    private String baseLine;
    private String score; // 경기 점수(기본값 0:0)

    private String endDate; // 경기 종료 시간
    private String startDate; // 시작 시간
    private LocalDateTime betStartTime;  // 베팅 시간

    private Long userId; // 유저 ID
    private String username; // 유저 이름
    private String nickname; // 유저 별명

    private int limitByLv; // 레벨별 한도
    private String betIp; // 베팅시점의 IP 주소



    @Column(columnDefinition = "BIGINT default 0")
    private Long sportsBalance;  //유저 보유금액
}
