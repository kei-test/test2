package GInternational.server.api.dto;

import GInternational.server.api.vo.AdminEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminListUpdateDTO {
    private String approveIp; // 로그인이 승인된 IP 주소
    private AdminEnum adminEnum; // 관리자 계정의 상태 설정 (예: 사용중, 사용불가)
    private String password;
}
