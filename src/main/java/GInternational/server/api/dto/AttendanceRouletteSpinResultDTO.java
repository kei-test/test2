package GInternational.server.api.dto;

import GInternational.server.api.vo.PaymentStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRouletteSpinResultDTO {
    private LocalDateTime spinDate; // 룰렛 돌린 시간
    private Long userId; // 룰렛을 돌린 유저
    private String rouletteName; // 룰렛 명
    private String rewardDescription; // 룰렛 세부명
    private String rewardValue; // 보상의 실제 값. 포인트의 경우 해당 포인트 수, 기프티콘의 경우 기프티콘의 종류
    private PaymentStatusEnum status; // 보상 지급 상태
    private LocalDateTime lastModifiedDate;
}
