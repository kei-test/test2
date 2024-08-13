package GInternational.server.api.dto;

import GInternational.server.api.vo.PasswordInquiryStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordInquiryRequestDTO {
    private String username;
    private String ownerName;
    private String phone;
    private String ip;
    private PasswordInquiryStatusEnum status;
}
