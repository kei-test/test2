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
public class AdminLoginSuccessRecordDTO {
    private Long id;
    private String username; // 관리자 아이디
    private String nickname; // 닉네임
    private String attemptIp; // IP 주소
    private String countryCode; // 국가 코드
    private String deviceType; // 디바이스 타입
    private LocalDateTime attemptDate; // 시도 날짜
    private String site;
}
