package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonRechargeTransaction;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmazonRechargeTransactionDTO {

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;

    public AmazonRechargeTransactionDTO(AmazonRechargeTransaction amazonRechargeTransaction) {
        this.processedAt = amazonRechargeTransaction.getProcessedAt();
    }
}
