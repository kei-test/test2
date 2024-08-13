package GInternational.server.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmazonRechargeRequestDTO {

    //유저의 충전 신청 전송 객체
    private long rechargeAmount;
    private long amazonBonus;
}
