package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TotalExchangeAmountDTO {
    private Long totalExchangeAmount; // 조회기간 동안의 전체 환전금액
}
