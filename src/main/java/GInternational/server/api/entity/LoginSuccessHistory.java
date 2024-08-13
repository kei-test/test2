package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "login_success_history")
public class LoginSuccessHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_success_history_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "login_ip")
    private String loginIp;

    @Column(name = "login_device")
    private String loginDevice;

    @Column(name = "login_date", updatable = false)
    private LocalDateTime loginDate;

    @Column(name = "login_url")
    private String loginUrl;
}
