package GInternational.server.api.entity;

import GInternational.server.api.vo.AmazonUserStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "admin_login_history")
@EntityListeners(AuditingEntityListener.class)
public class AdminLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 기본 키

    private String username; // 관리자로 등록된 아이디
    private String nickname; // 관리자로 등록된 닉네임
    private String role; // 관리자로 등록된 Role (예: admin, manager)
    private String status; // 관리자의 상태 (예: 활성, 비활성)
    private String site = "test";

    @Column(name = "created_at")
    private LocalDateTime createdAt; // 관리자 계정의 생성 시간

    @Column(name = "visit_count")
    private long visitCount; // 해당 관리자의 로그인 성공 횟수

    @Column(name = "last_accessed_ip")
    private String lastAccessedIp; // 마지막으로 로그인 시도한 IP 주소

    @Column(name = "last_visit")
    private LocalDateTime lastVisit; // 마지막으로 로그인 성공한 시간

    @Column(name = "approve_ip")
    private String approveIp; // 로그인이 승인된 IP 주소

    @Enumerated(EnumType.STRING)
    @Column(name = "amazon_user_status")
    private AmazonUserStatusEnum amazonUserStatus;  // 대기-정상-정지 // 관리자 계정의 상태 설정 (예: 사용가능/사용불가) - 사용불가이면 로그인 차단 - "로그인이 차단된 계정입니다"

    @Column(name = "password_changed")
    private String passwordChanged; // 비밀번호 변경 여부

    @Column(name = "attempt_ip")
    private String attemptIp; // 로그인 시도한 IP 주소

    @Column(name = "user_status")
    private String userStatus; // 관리자의 현재 상태 (예: 활성, 비활성)

    @Column(name = "attempt_date")
    private LocalDateTime attemptDate; // 로그인 시도한 날짜와 시간

    @Column(name = "login_result")
    private String loginResult; // 로그인 결과 (성공/실패)

    @Column(name = "country_code")
    private String countryCode; // 국가 코드

    @Column(name = "device_type")
    private String deviceType; // 단말기 타입

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // User 엔티티에 대한 참조
}
