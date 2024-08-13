package GInternational.server.api.utilities;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditContext {
    private String targetId;         // 대상 id
    private String username;         // 대상 유저네임
    private String ip;               // 관리자의 ip
    private String details;          // 상세 내용
    private String adminUsername;    // 관리자의 유저네임
    private LocalDateTime timestamp; // 작성시간
}
