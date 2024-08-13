package GInternational.server.api.dto;

import GInternational.server.api.vo.PasswordInquiryStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordInquiryResponseDTO {
    private Long id;
    private String username;
    private String ownerName;
    private String ip;
    private String phone;
    private LocalDateTime createdAt;
    private PasswordInquiryStatusEnum status;
    private String adminMemo;
}
