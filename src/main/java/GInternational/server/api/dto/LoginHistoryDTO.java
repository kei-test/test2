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
public class LoginHistoryDTO {


    private String attemptUsername;
    private String attemptNickname;
    private String attemptPassword;
    private String attemptIP;
    private String attemptDevice;
    private String attemptNation;
    private String attemptUrl;

    private LocalDateTime attemptDate;
}
