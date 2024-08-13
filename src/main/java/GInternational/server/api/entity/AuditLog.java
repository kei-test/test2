package GInternational.server.api.entity;

import GInternational.server.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action; // 제목
    @Column(columnDefinition = "MEDIUMTEXT")
    private String details; // 처리내용
    private String username; // 관리자가 활동한 대상의 username (대상인 유저가 있을경우에만 저장)
    @Column(name = "admin_username")
    private String adminUsername; // 관리자의 username
    @Column(name = "target_id")
    private String targetId; // 처리대상 사용자의 id
    private String ip; // IP
    private LocalDateTime timestamp; // 일시

    @JsonIgnore  // 순환 참조로 인해 설정
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
