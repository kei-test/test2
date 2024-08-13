package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginStatisticDTO {
    private LocalDate date;
    private long visitCount;
    private long rechargedCount;
    private long exchangeCount;
    private long debitCount;
    private long createUserCount;

}
