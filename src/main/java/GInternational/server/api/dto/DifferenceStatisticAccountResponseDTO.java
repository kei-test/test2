package GInternational.server.api.dto;

import GInternational.server.api.vo.ManagementAccountEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DifferenceStatisticAccountResponseDTO {

    private Long id;
    private String turn; // 순번
    private ManagementAccountEnum usage; // 용도 "카지노 캐시, 예비, 앞방, 중간방, 뒷방, 현금"
    private String ownerName; // 예금주
    private Long number; // 계좌번호
    private String source; // 출처
    private boolean isUse; // 사용여부
    private String transferLimit; // 이체한도
    private long currentMoney; // 보유금액
    private String memo; // 메모
}
