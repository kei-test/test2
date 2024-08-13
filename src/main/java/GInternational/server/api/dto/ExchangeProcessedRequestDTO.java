package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExchangeProcessedRequestDTO {

    // 충전 건에 대한 승인 데이터를 담은 전송 객체
    private long userSportsBalance;  //충전 승인처리 후 보유 머니의 총 합계

}
