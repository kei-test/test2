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
@Entity(name = "login_history")
@EntityListeners(AuditingEntityListener.class)
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "attempt_username")
    private String attemptUsername;

    @Column(name = "attempt_nickname")
    private String attemptNickname;

    @Column(name = "attempt_password")
    private String attemptPassword;

    @Column(name = "attempt_ip")
    private String attemptIP;

    @Column(name = "attempt_device")
    private String attemptDevice;

    @Column(name = "attempt_nation")
    private String attemptNation;

    @Column(name = "attempt_url")
    private String attemptUrl;

    @CreatedDate
    @Column(name = "attempt_date", updatable = false)
    private LocalDateTime attemptDate;
}
