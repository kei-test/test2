package GInternational.server.api.entity;

import GInternational.server.api.vo.LevelUpTransactionEnum;
import GInternational.server.api.vo.ReferredGubunEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "level_up")
public class LevelUp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "level_up_id")
    private Long id;

    @Column(name = "referred_by")
    private String referredBy; // 추천한 유저의 ID

    @Enumerated(EnumType.STRING)
    @Column(name = "referred_gubun")
    private ReferredGubunEnum referredGubun; // 추천구분 (추천인, 추천코드)

    private Long userId;     // 레벨업 신청자의 pk값
    private String username; // 레벨업 신청자의 ID
    private String nickname; // 레벨업 신청자의 닉네임
    @Column(name = "apply_lv")
    private int applyLv;     // 레벨업 신청 레벨
    @Column(name = "target_lv")
    private int targetLv;    // 레벨업 대상 레벨
    private String memo;     // 메모

    @Enumerated(EnumType.STRING)
    private LevelUpTransactionEnum status; // WAITING(접수), APPROVAL(승인), CANCELLATION(거부)

    @Column(name = "deposit_total", columnDefinition = "BIGINT default 0")
    private long depositTotal;    // 총 입금액
    @Column(name = "withdraw_total", columnDefinition = "BIGINT default 0")
    private long withdrawTotal;   // 총 출금액
    @Column(name = "total_settlement", columnDefinition = "BIGINT default 0")
    private long totalSettlement; // 총 정산액 (depositTotal - balance & withdrawTotal = totalSettlement)

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // 신청일
    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 승인 혹은 취소일
}
