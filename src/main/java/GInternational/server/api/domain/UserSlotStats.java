package GInternational.server.api.domain;

import java.math.BigDecimal;

public interface UserSlotStats {
    Integer getUserId();
    BigDecimal getTotalBet();
    BigDecimal getTotalWin();
}
