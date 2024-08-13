package GInternational.server.api.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "roulette_settings")
@EntityListeners(AuditingEntityListener.class)
public class RouletteSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roulette_settings_id")
    private Long id;

    @Column(name = "roulette_name")
    private String rouletteName; // 룰렛명
    @Column(name = "reward_value")
    private String rewardValue; // 상품값
    @Column(name = "reward_description")
    private String rewardDescription; // 상품 세부명
    @Column(name = "max_quantity")
    private long maxQuantity; // 최대 지급 개수
    @Column(name = "original_max_quantity")
    private long originalMaxQuantity; // 관리자가 마지막으로 업데이트한 최대 지급개수
    private double probability; // 확률

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate;
}