package GInternational.server.api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "password_change_transaction")
public class PasswordChangeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String username;           // 비밀번호 변경 신청유저의 ID
    private String nickname;           // 비밀번호 변경 신청유저의 닉네임
    private String status;             // 트랜잭션 상태 (대기, 완료, 취소)
    private String phone;              // 신청건에 대한 상태값
    @Column(name = "owner_name")
    private String ownerName;          // 비밀번호 변경 신청유저의 예금주
    private Long number;               // 비밀번호 변경 신청유저의 계좌번호
    private String ip;                 // 신청당시의 IP

    @Column(name = "last_accessed_ip")
    private String lastAccessedIp;     // 비밀번호 변경 신청유저의 최근 접속 IP

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // 신청시간
    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 처리시간
    @Column(name = "processed_username")
    private String processedUsername;  // 처리자

    @Column(name = "current_password")
    private String currentPassword;    // 현재 비밀번호
    @Column(name = "new_password")
    private String newPassword;        // 변경신청 할 비밀번호
}
