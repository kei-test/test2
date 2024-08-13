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
public class AmazonExchangeTransactionApprovedDTO {
    private Long id;
    private long approvedAmount;
    private LocalDateTime approvedAt;
}
