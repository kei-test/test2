package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BetLogResponseDTO {
    private Long userId;
    private String username;
    private String nickname;
    private String gameName;
    private String gameType;
    private LocalDateTime betTime;
    private BigDecimal betAmount;
    private BigDecimal winAmount;
    private String result;
    private String prdName;
}