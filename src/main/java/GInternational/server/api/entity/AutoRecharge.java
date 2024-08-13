package GInternational.server.api.entity;

import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.TransactionGubunEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "auto_recharge")
public class AutoRecharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_recharge_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String username;     // 유저아이디
    private String site;         // 사이트
    @Column(name = "number")
    private Long number;         // 계좌번호
    @Column(name = "bank_name")
    private String bankName;     // 은행명
    @Column(name = "owner_name")
    private String ownerName;    // 예금주

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt; // 충전요청 시간
    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 업데이트 시간

    private LocalDateTime timestamp; // 문자 수신시간
    private String message;          // 메시지 본문
    private String depositor;        // 입금자
    private String amount;           // 입금금액

    private String status;           // 문자 수신/미수신 상태값

    @Builder
    public AutoRecharge(Long id, Long userId, String username, String site, Long number, String bankName,
                        String ownerName, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime timestamp,
                        String message, String depositor, String amount, String status) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.site = site;
        this.number = number;
        this.bankName = bankName;
        this.ownerName = ownerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.timestamp = timestamp;
        this.message = message;
        this.depositor = depositor;
        this.amount = amount;
        this.status = status;
    }
}
