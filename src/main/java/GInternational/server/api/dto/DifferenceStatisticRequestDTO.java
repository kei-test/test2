package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DifferenceStatisticRequestDTO {

    private String bigo; // "비고"
    private long operatingExpense; // "운영비"
    private long wonExchange; // "원환전"
    private long commissionPercent; // "수수료율 %" 소숫점x 정수만 입력
    private long dongExchange; // "동환전"
}
