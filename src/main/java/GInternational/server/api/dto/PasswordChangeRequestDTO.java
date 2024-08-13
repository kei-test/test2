package GInternational.server.api.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeRequestDTO {
    private String currentPassword; // 현재 비밀번호
    private String newPassword; // 새 비밀번호
}
