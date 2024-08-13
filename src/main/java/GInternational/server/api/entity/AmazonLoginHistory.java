package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "amazon_login_history")
@EntityListeners(AuditingEntityListener.class)
public class AmazonLoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gubun; // 관리자, 관리자-없는계정, 파트너, 파트너-없는계정, 유저, 유저-없는계정

    @Column(name = "attempt_username")
    private String attemptUsername;

    @Column(name = "attempt_nickname")
    private String attemptNickname;

    @Column(name = "attempt_password")
    private String attemptPassword;

    private String result; // 접속 실패, 성공

    @Column(name = "attempt_ip")
    private String attemptIP;

    @CreatedDate
    @Column(name = "attempt_date", updatable = false)
    private LocalDateTime attemptDate;
}
