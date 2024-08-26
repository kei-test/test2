package GInternational.server.amzn.dto.log;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class AmznLogDTO {

    //아마존총판 시스템 로그
    private String gubun;
    private String attemptUsername;
    private String attemptNickname;
    private String attemptPassword;
    private String result;
    private String attemptIp;
    private LocalDateTime attemptDate;


    @QueryProjection
    public AmznLogDTO(String gubun, String attemptUsername, String attemptNickname, String attemptPassword, String result, String attemptIp, LocalDateTime attemptDate) {
        this.gubun = gubun;
        this.attemptUsername = attemptUsername;
        this.attemptNickname = attemptNickname;
        this.attemptPassword = attemptPassword;
        this.result = result;
        this.attemptIp = attemptIp;
        this.attemptDate = attemptDate;
    }
}
