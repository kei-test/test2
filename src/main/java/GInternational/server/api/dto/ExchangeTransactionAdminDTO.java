package GInternational.server.api.dto;

import GInternational.server.api.entity.ExchangeTransaction;
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
public class ExchangeTransactionAdminDTO {
    private Long id;
    private int lv;
    private long exchangeAmount;
    private UserIdResponseDTO user;
    private WalletDetailDTO wallet;
    private String distributor;
    private TransactionEnum status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    private String username;
    private String nickname;
    private String phone;


    public ExchangeTransactionAdminDTO(ExchangeTransaction exchangeTransaction) {
        this.user = new UserIdResponseDTO(exchangeTransaction.getUser().getId());
        this.wallet = new WalletDetailDTO(exchangeTransaction.getUser().getWallet());
    }
}
