package GInternational.server.api.entity;

import GInternational.server.api.vo.CouponTypeEnum;
import GInternational.server.api.vo.CouponTransactionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "coupon_transaction")
@EntityListeners(AuditingEntityListener.class)
public class CouponTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_transaction_id")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String site = "";
    private String memo = "";

    private String username; // 회원가입시 입력한 아이디
    private String nickname; // 회원가입시 입력한 닉네임
    private long point; // 지급할 포인트
    @Column(name = "sports_balance")
    private long sportsBalance; // 지급할 캐시
    @Column(name = "expiration_date_time")
    private LocalDateTime expirationDateTime; // 머니쿠폰 유효기간

    @Column(name = "coupon_name")
    private String couponName; // 쿠폰명 (머니쿠폰, 행운복권)

    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_type_enum")
    private CouponTypeEnum couponTypeEnum; // 쿠폰타입 (머니쿠폰, 행운복권)

    @Enumerated(EnumType.STRING)
    private CouponTransactionEnum status; // 신청건에 대한 처리 현황

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt; // 쿠폰 생성 시간

    @LastModifiedDate
    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt; // 처리 시간
}
