package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminPasswordChangeReqDTO {
    private String username;
    private String currentPassword; // 현재 비밀번호
    private String newPassword; // 새 비밀번호
    private String checkNewPassword; // 새 빌민번호 확인
}
