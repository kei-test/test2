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
public class AdminLoginResultDTO {

    private Long id;
    private String username; // 유저 ID
    private String attemptIp; // 로그인 시도한 IP 주소
    private LocalDateTime attemptDate; // 로그인 시도한 날짜와 시간
    private String loginResult; // 로그인 결과 (성공/실패)
    private String countryCode; // 국가 코드
    private String deviceType; // 단말기 타입
    private String site;
}
