package GInternational.server.api.entity;

import GInternational.server.api.vo.PointLogCategoryEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "point_log")
public class PointLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_log_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    private String username;
    private String nickname;
    private Long point; // 적립된 포인트
    @Column(name = "final_point")
    private Long finalPoint; // 최종 포인트
    @Column(name = "created_at")
    private LocalDateTime createdAt; // 포인트 적립된 시간
    @Enumerated(EnumType.STRING)
    private PointLogCategoryEnum category; // 룰렛, 출석체크룰렛, 슬롯롤링적립 등등
    private String memo; // 메모
    private String ip; // user의 ip
}