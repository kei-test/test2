package GInternational.server.api.entity;


import GInternational.server.api.vo.BetFoldCountEnum;
import GInternational.server.api.vo.BetFoldTypeEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor()
@NoArgsConstructor
@Setter
@Getter
@Entity(name = "bet_history")
public class BetHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bet_history_id")
    private Long id;
    @Column(name = "sport_name")
    private String sportName;    // 종목명
    @Column(name = "location_name")
    private String locationName; // 지역명
    @Column(name = "league_name")
    private String leagueName;   // 리그명
    @Column(name = "match_status")
    private String matchStatus;  // 경기상태 구분 // "1" : 경기 시작 전 / "2" : 경기 중 / "3" : 경기종료
    @Column(name = "bet_status")
    private String betStatus;    // 베팅 상태 구분 (정상, 삭제됨 등등)
    @Column(name = "home_name")
    private String homeName;     // 홈팀
    @Column(name = "away_name")
    private String awayName;     // 원정팀

    //베팅 내역 작업필드
    @Column(name = "bet_group_id")
    private Long betGroupId;
    @Column(name = "match_id")
    private String matchId;      // 경기 ID
    @Column(name = "idx")
    private String idx;          // 베팅 id
    @Column(name = "bet_team")
    private String betTeam;      // 베팅한 팀
    @Column(name = "bet")
    private String bet;          // 베팅금
    @Column(name = "price")
    private String price;        // 배당률
    @Column(name = "bet_reward")
    private String betReward;    // 당첨금
    @Column(name = "settlement")
    private String settlement;
    @Column(name = "processed_at")
    private LocalDateTime processedAt;     // 결과 처리 시간
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatusEnum orderStatus;   // 진행, 대기, 적특, 적중, 낙첨
    @Enumerated(EnumType.STRING)
    @Column(name = "bet_fold_type")
    private BetFoldTypeEnum betFoldType;   // 단폴, 3폴, 5폴, 7폴
    @Column(name = "cancelled_by")
    private String cancelledBy;            // 관리자에게 취소된 베팅인지, 유저에게 취소된 베팅인지 구분
    @Enumerated(EnumType.STRING)
    @Column(name = "bet_fold_count")
    private BetFoldCountEnum betFoldCount; // 1~15개의 폴더
    @Enumerated(EnumType.STRING)
    @Column(name = "bet_type")
    private BetTypeEnum betType;           // 승무패, 언오버, 핸디캡, 스페셜1, 스페셜2


    //배당
    @Column(name = "market_name")
    private String marketName;   // 배당명
    @Column(name = "win_rate")
    private String winRate;      // 승배당
    @Column(name = "draw_rate")
    private String drawRate;     // 무배당
    @Column(name = "lose_rate")
    private String loseRate;     // 패배당


    @Column(name = "bet_ip")
    private String betIp;  // 베팅시점의 ip주소
    @Column(name = "limit_by_lv")
    private int limitByLv; // 레벨별 한도
    @Column(name = "start_date")
    private String startDate;    // 경기 시작 시간
    @Column(name = "bet_start_time")
    private LocalDateTime betStartTime; // 베팅 시작 시간
    @Column(name = "deleted")
    private boolean deleted = false;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    @Column(name = "read_status")
    private String readStatus;    // 베팅내역 확인 상태값 (베팅시 기본값 "미확인" / 베팅상세내역 조회될때 "확인"으로 변경)
    @Column(name = "read_by")
    private String readBy;        // 확인 처리자
    @Column(name = "read_at")
    private LocalDateTime readAt; // 확인 시간


    @Column(name = "fail_bonus_col",nullable = false,columnDefinition = "varchar(1) default 'N' ")
    private String failBonusCol;  //낙첨 시 낙첨 보너스 지급 여부

    @Column(name = "cron_api",nullable = false, columnDefinition = "varchar(1) default 'N' ")
    private String cronApi; //자동 정산 시 로그를 남기기 위한 파라미터

    @Column(name = "api",nullable = false, columnDefinition = "varchar(1) default 'N' ")
    private String api; //결과 수정 후 정산 여부 확인 파라미터

    //유저 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;  //참조 유저 정보


    public BetHistory(Long betGroupId) {
        this.betGroupId = betGroupId;
    }

    public BetHistory(String idx, String settlement) {
        this.idx = idx;
        this.settlement = settlement;
    }

    public BetHistory(String betReward,User user) {
        this.betReward = betReward;
        this.user = user;
    }

    public BetHistory(String settlement, OrderStatusEnum orderStatus, LocalDateTime processedAt) {
        this.settlement = settlement;
        this.orderStatus = orderStatus;
        this.processedAt = processedAt;
    }


    public static BetHistory insertBetReward(String betReward, Long betGroupId) {
        BetHistory betHistory = new BetHistory();
        betHistory.setBetReward(betReward);
        betHistory.setBetGroupId(betGroupId);
        return betHistory;
    }
}
