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
public class LoginInfoResponseDTO {

    private Long id;
    private Long userId;
    private String username;           // 로그인 ID
    private String nickname;           // 닉네임
    private String distributor;        // 최상위파트너 아마존의 대본사, 또는 DST 파트너유저
    private String store;              // 하위파트너 아마존의 본사,부본사, 총판, 매장
    private String AccessedIp;         // 로그인 IP
    private String AccessedDevice;     // 로그인 단말기 구분 (M = mobile, P = pc)
    private LocalDateTime lastVisit;   // 로그인 시간
    private String site;
}
