package GInternational.server.api.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "login_info")
public class LoginInfo {

    /**
     * 어드민 19번 "로그인 정보"
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "login_info_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String username;           // 로그인 ID
    private String nickname;           // 닉네임
    private String distributor;        // 최상위파트너 아마존의 대본사, 또는 DST 파트너유저
    private String store;              // 하위파트너 아마존의 본사,부본사, 총판, 매장
    @Column(name = "accessed_ip")
    private String accessedIp;         // 로그인 IP
    @Column(name = "accessed_device")
    private String accessedDevice;     // 로그인 단말기 구분 (M = mobile, P = pc)
    @Column(name = "last_visit")
    private LocalDateTime lastVisit;   // 로그인 시간
}
