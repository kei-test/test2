package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginInfoRequestDTO {

    private String username;           // 로그인 ID
    private String nickname;           // 닉네임
    private String distributor;        // 총판 (wind, mega 등)
    private String AccessedIp;         // 로그인 IP
    private String AccessedDevice;     // 로그인 단말기 구분 (M = mobile, P = pc)
    private LocalDateTime lastVisit;   // 로그인 시간
}
