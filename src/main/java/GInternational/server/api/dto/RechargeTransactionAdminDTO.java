package GInternational.server.api.dto;

import GInternational.server.api.entity.RechargeTransaction;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.TransactionGubunEnum;
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
public class RechargeTransactionAdminDTO {

    private Long id;
    private TransactionGubunEnum gubun;
    private int lv;
    private String username;
    private String nickname;
    private String phone;
    private String ownerName;
    private int rechargeAmount;
    private UserIdResponseDTO user;
    private int bonus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    private String distributor;
    private TransactionEnum status;


    public RechargeTransactionAdminDTO(RechargeTransaction rechargeTransaction) {
        this.user = new UserIdResponseDTO(rechargeTransaction.getUser().getId());
    }
}
