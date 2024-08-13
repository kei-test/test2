package GInternational.server.api.dto;

import GInternational.server.api.vo.UserGubunEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReferralInfoDTO {
    private Long id;
    private String username;
    private String nickname;
    private String status; // 상태를 문자열로 표현 (예: 상태)
    private UserGubunEnum userGubunEnum;
    private LocalDateTime createdAt;
}
