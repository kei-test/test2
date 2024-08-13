package GInternational.server.api.dto;

import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.TransactionGubunEnum;
import GInternational.server.api.vo.UserGubunEnum;
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
public class RechargeTransactionResDTO {

    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String phone;
    private TransactionGubunEnum gubun;
    private long rechargeAmount;
    private long remainingSportsBalance; // 충전 승인 처리 후 스포츠머니
    private int bonus;
    private int remainingPoint;
    private String message;

    private TransactionEnum status;
    private String ip;

    private String site;

    private String depositor;
    private UserGubunEnum userGubunEnum;
    private WalletDetailDTO wallet;

    private int lv;

    private String distributor;
    private String store;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime exchangeProcessedAt;
}
