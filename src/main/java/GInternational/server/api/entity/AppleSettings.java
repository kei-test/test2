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
@Entity(name = "apple_settings")
@EntityListeners(AuditingEntityListener.class)
public class AppleSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reward_name", nullable = false)
    private String rewardName; // 보상명
    @Column(name = "reward_value", nullable = false)
    private String rewardValue; // 보상값
    @Column(name = "reward_description", nullable = false)
    private String rewardDescription; // 보상 세부명
    @Column(name = "max_quantity", nullable = false)
    private long maxQuantity; // 최대 지급 개수
    @Column(name = "original_max_quantity", nullable = false)
    private long originalMaxQuantity; // 관리자가 마지막으로 업데이트한 최대 지급개수
    @Column(nullable = false)
    private double probability; // 확률

    @CreatedDate
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate; // 세팅값이 최초 생성된 시간

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime lastModifiedDate; // 세팅값이 마지막으로 수정된 시간
}
