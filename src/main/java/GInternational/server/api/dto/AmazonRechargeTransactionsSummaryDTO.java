package GInternational.server.api.dto;

import GInternational.server.api.utilities.AmazonBigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AmazonRechargeTransactionsSummaryDTO {
    private long totalRechargeAmount;
    @JsonSerialize(using = AmazonBigDecimalSerializer.class)
    private BigDecimal averageRechargeAmount;
}
