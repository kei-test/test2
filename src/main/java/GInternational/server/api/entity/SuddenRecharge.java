package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "sudden_recharge")
public class SuddenRecharge {

    /**
     *  피그마 105번 "특수 이벤트 / 돌발(시간) 충전 이벤트
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sudden_recharge_id")
    private Long id;

    @Column(name = "condition_1")
    private Long condition1; // 조건 1
    @Column(name = "point_1")
    private Long point1;     // 조건 1의 지급 포인트

    @Column(name = "condition_2")
    private Long condition2; // 조건 2
    @Column(name = "point_2")
    private Long point2;     // 조건 2의 지급 포인트

    @Column(name = "condition_3")
    private Long condition3; // 조건 3
    @Column(name = "point_3")
    private Long point3;     // 조건 3의 지급 포인트

    @Column(name = "start_date_time")
    private LocalDateTime startDateTime; // 이벤트 시작시간
    @Column(name = "end")
    private LocalDateTime endDateTime;   // 이벤트 종료시간

    @Column(nullable = false)
    private boolean enabled; // 적용 여부
}
