package GInternational.server.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String username;
    private String nickname;
    private String name;
    private int lv;
    private String lastAccessedIp;
    private String role;
    private long visitCount;
    private long casinoBalance;
    private long sportsBalance;
    private long point;
    private Long walletId;
    private long unreadMessageCount;

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime visitLog;
}