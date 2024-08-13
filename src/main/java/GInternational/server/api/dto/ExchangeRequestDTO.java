package GInternational.server.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExchangeRequestDTO {

    //유저의 충전 신청 전송 객체
    private String ownerName; // 예금주
    private String bankPassword; // 환전 비밀번호
    private long exchangeAmount;

}
