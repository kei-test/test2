package GInternational.server.api.dto;

import GInternational.server.api.vo.AdminEnum;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdminListResDTO {

    private Long id;
    private String username; // 관리자로 등록된 아이디
    private String nickname; // 관리자로 등록된 닉네임
    private String role; // 관리자로 등록된 Role (예: admin, manager)
    private AdminEnum adminEnum; // 사용중, 사용불가
    private LocalDateTime createdAt; // 관리자 계정의 생성 시간
    private long visitCount; // 해당 관리자의 로그인 성공 횟수
    private String lastAccessedIp; // 마지막으로 로그인 시도한 IP 주소
    private LocalDateTime lastVisit; // 마지막으로 로그인 성공한 시간
    private String approveIp; // 로그인이 승인된 IP 주소
    private String passwordChanged; // 비밀번호 변경 여부
}
