package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonExchangeTransaction;

import GInternational.server.api.vo.TransactionEnum;

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
public class AmazonExchangeTransactionAdminDTO {
    private Long id;
    private long lv;
    private long exchangeAmount;
    private UserIdResponseDTO user;
    private TransactionEnum status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    private String username;
    private String nickname;
    private String phone;


    public AmazonExchangeTransactionAdminDTO(AmazonExchangeTransaction amazonExchangeTransaction) {
        this.user = new UserIdResponseDTO(amazonExchangeTransaction.getUser().getId());
    }
}
