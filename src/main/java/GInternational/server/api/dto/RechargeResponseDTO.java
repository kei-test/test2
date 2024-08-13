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
public class RechargeResponseDTO {
    private Long id;
    private int sportsBalance;  //보유 스포츠머니 총 합계
    private int rechargeAmount;  //충전 신청 포인트 금액 ex) 15000 -> 0 으로 전환
    private int bonus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private UserProfileDTO user;



    public RechargeResponseDTO(RechargeTransaction wallet) {
        this.user = new UserProfileDTO(wallet.getUser());
    }
}
