package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class UserSlotBetWinDTO {
    private String username;
    private BigDecimal betAmount;
    private BigDecimal winAmount;
    private BigDecimal total;
}
