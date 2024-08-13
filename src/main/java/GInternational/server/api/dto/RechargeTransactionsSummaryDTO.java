package GInternational.server.api.dto;

import GInternational.server.api.utilities.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RechargeTransactionsSummaryDTO {
    private long totalRechargeAmount;
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal averageRechargeAmount;
    private long totalAllTimeRechargeAmount;
}
