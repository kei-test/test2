package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginInquiryListDTO {

    private Long id;
    private String writerName;
    private String ownerName;
    private String phone;
    private String ip;
    private String memo;
    private String answerStatus;
    private String site;
    private LocalDateTime createdAt;
}
