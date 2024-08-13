package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.api.vo.AmazonTransactionEnum;
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
public class AmazonRechargeTransactionAdminDTO {

    private Long id;
    private long lv;
    private String username;
    private String nickname;
    private String phone;
    private String ownerName;
    private long rechargeAmount;
    private UserIdResponseDTO user;
    private long bonus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    private AmazonTransactionEnum status;

    public AmazonRechargeTransactionAdminDTO(AmazonRechargeTransaction amazonRechargeTransaction) {
        this.user = new UserIdResponseDTO(amazonRechargeTransaction.getUser().getId());
    }
}
