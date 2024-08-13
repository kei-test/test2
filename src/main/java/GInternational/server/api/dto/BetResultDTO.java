package GInternational.server.api.dto;


import GInternational.server.api.vo.BetFoldCountEnum;
import GInternational.server.api.vo.BetFoldTypeEnum;
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
public class BetResultDTO {
    private Long betGroupId; // No.
    private Long userId; // User 엔티티 참조
    private BetFoldTypeEnum betFoldType; // 벳타입 단폴, 3폴, 5폴, 7폴
    private BetTypeEnum betType; // 타입 (승무패, 조합베팅, 스페셜, 인플레이 등)
    private LocalDateTime betStartTime; // 베팅시간
    private String bet; // 베팅금
    private String price; // 배당률
    private String betReward; // 당첨금
    private LocalDateTime processedAt; // 정산시간
    private BetFoldCountEnum betFoldCount; // 폴더 갯수
    private OrderStatusEnum orderStatus;
    private String status; // 상태 // "1" : 베팅가능 상태 / "2" : 베팅불가 상태 / "3" : 경기종료 상태
    private String betIp; // IP
    private int sequenceNumber; // betStartTime을 기준으로한 betGroupId의 순번
}
