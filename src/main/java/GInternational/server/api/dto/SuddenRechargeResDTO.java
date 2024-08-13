package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SuddenRechargeResDTO {

    private Long id;

    private Long condition1; // 조건 1
    private Long point1;     // 조건 1의 지급 포인트

    private Long condition2; // 조건 2
    private Long point2;     // 조건 2의 지급 포인트

    private Long condition3; // 조건 3
    private Long point3;     // 조건 3의 지급 포인트

    private LocalDateTime startDateTime; // 이벤트 시작시간
    private LocalDateTime endDateTime;   // 이벤트 종료시간

    private boolean enabled; // 적용 여부
}
