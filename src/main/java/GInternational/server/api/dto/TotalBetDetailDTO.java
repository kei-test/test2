package GInternational.server.api.dto;


import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalBetDetailDTO {
    private String matchId;
    private Long betGroupId;
    private String sportName;
    private String leagueName;
    private String startDate;
    private String homeName;
    private String awayName;
    private String price;
    private String bet; // 베팅금
    private String betTeam;
    private String marketName; // 배당명

    private LocalDateTime betStartTime; // 베팅 시작 시간
    private OrderStatusEnum orderStatus; // 진행 상태
    private BetTypeEnum betType; // 베팅 타입
    private String readStatus; // 조회 상태값 (미확인, 확인)
}
