package GInternational.server.api.dto;

import GInternational.server.api.entity.AutoDepositTransaction;
import GInternational.server.api.vo.AppStatus;
import GInternational.server.api.vo.TransactionEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AutoDepositTransactionAdminResDTO {
    private Long id;
    private int lv;
    private String username;
    private String nickname;
    private String ownerName;
    private long amount;
    private long bonus;
    private String distributor;
    private UserProfileDTO user;
    //private WalletDetailDTO wallet;
    private AppStatus appStatus;
    private TransactionEnum status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;  //처리 일자
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime LastDepositDate;  //마지막 입금일
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime lastWithdrawalDate;  // 마지막 출금일


    public AutoDepositTransactionAdminResDTO(AutoDepositTransaction autoDepositTransaction) {
        this.user = new UserProfileDTO(autoDepositTransaction.getUser());
        //this.wallet = new WalletDetailDTO(autoDepositTransaction.getUser().getWallet());
    }
}
