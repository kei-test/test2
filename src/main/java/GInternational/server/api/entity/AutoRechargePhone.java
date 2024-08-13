package GInternational.server.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "auto_recharge_phone")
public class AutoRechargePhone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_recharge_bank_account_id")
    private Long id;

    @Column(name = "phone")
    private String phone; // 자동충전 사용하고있는 핸드폰 번호

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 핸드폰번호 등록시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정된 시간 (가장 최근 자동충전으로 갱신된 시간)
}
