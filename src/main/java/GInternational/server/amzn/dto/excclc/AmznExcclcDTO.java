package GInternational.server.amzn.dto.excclc;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AmznExcclcDTO {

    private Long userId;
    private long num;
    private String partnerType;
    private String username;
    private String amazonCode;
    private double casinoRolling;
    private double slotRolling;
    private double totalRolling;
    private String referredBy;
    private long excclcAmount; // 정산금액
    private long actAmount; // 실수령

    @QueryProjection
    public AmznExcclcDTO(Long userId,String partnerType, String username, String amazonCode, double casinoRolling, double slotRolling, String referredBy) {
        this.userId = userId;
        this.partnerType = partnerType;
        this.username = username;
        this.amazonCode = amazonCode;
        this.casinoRolling = casinoRolling;
        this.slotRolling = slotRolling;
        this.referredBy = referredBy;
    }
}
