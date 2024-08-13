package GInternational.server.l_sport.batch.job.dto.order;

import GInternational.server.api.vo.BetFoldTypeEnum;
import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ResponseDTO {

    //단폴/다폴 주문 건들의 합계 사항이 들어있는 외부 DTO
    private Long betGroupId;
    private String bet; //베팅 가격
    private String totalRate; //총 배당률
    private String expectedProfit; //에상 수익
    private String realProfit; //실제 수익
    private LocalDateTime betStartTime; //베팅한 시간
    private String orderResult; //주문 결과
    private BetFoldTypeEnum betFoldTypeEnum; //폴더 타입 (단폴, 3폴, 5폴, 7폴)
    private String eventRate;
    private String readStatus;
    private String readBy;
    private LocalDateTime readAt;
    private UserMonitoringStatusEnum monitoringStatus;
    private String betStatus;
    private Boolean deleted;
    private LocalDateTime deletedAt;
    private List<DetailResponseDTO> list; //그룹 아이디에 속한 베팅 리스트


    public ResponseDTO(String eventRate) {
        this.eventRate = eventRate;
    }
}
