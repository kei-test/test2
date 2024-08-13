package GInternational.server.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "auto_recharge_bank_account")
public class AutoRechargeBankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_recharge_bank_account_id")
    private Long id;

    @Column(name = "bank_name")
    private String bankName; // 은행명
    @Column(name = "number")
    private String number;   // 계좌번호
    @Column(name = "is_use")
    private Boolean isUse;   // 사용여부 (true: 사용 / false: 미사용)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 계좌 등록 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정된 시간
}
