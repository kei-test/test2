package GInternational.server.api.dto;


import GInternational.server.api.vo.BetFoldCountEnum;
import GInternational.server.api.vo.BetFoldTypeEnum;
import GInternational.server.api.vo.BetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalBetListResDTO {
    private Long betGroupId; // No.
    private String username; // ID
    private String nickname; // 닉네임

    private BetFoldTypeEnum betFoldType; // 벳타입 단폴, 3폴, 5폴, 7폴
    private BetTypeEnum betType; // 타입 (승무패, 조합베팅, 스페셜, 인플레이 등)
    private LocalDateTime betStartTime; // 베팅시간

    private String bet; // 베팅금
    private String price; // 배당률
    private String result; // 결과

    private LocalDateTime processedAt; // 정산시간
    private BetFoldCountEnum betFoldCount; // 폴더 갯수
    private String matchStatus;
    private String ip; // IP

    private String betReward; // 당첨금
    private int sequenceNumber; // betStartTime을 기준으로한 betGroupId의 순번
}
