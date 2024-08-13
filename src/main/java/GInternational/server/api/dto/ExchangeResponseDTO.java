package GInternational.server.api.dto;

import GInternational.server.api.entity.RechargeTransaction;
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
public class ExchangeResponseDTO {
    private Long id;
    private long sportsBalance;  //보유 머니 총 합계
    private long rechargeAmount;  //충전 신청 포인트 금액 ex) 15000 -> 0 으로 전환
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private UserProfileDTO user;


    public ExchangeResponseDTO(RechargeTransaction wallet) {
        this.user = new UserProfileDTO(wallet.getUser());
    }
}
