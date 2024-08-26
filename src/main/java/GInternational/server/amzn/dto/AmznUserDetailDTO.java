package GInternational.server.amzn.dto;

import GInternational.server.api.vo.AmazonUserStatusEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznUserDetailDTO {


    private AmazonUserStatusEnum amazonUserStatus;
    private int lv;
    private String password;
    private String phone;
    private String ownerName;
    private String bankName;
    private Long number;
    private String amazonCode;
    private double casinoRolling;
    private double slotRolling;


    @QueryProjection
    public AmznUserDetailDTO(AmazonUserStatusEnum amazonUserStatus, int lv, String password, String phone, String ownerName, String bankName, Long number, String amazonCode, double casinoRolling, double slotRolling) {
        this.amazonUserStatus = amazonUserStatus;
        this.lv = lv;
        this.password = password;
        this.phone = phone;
        this.ownerName = ownerName;
        this.bankName = bankName;
        this.number = number;
        this.amazonCode = amazonCode;
        this.casinoRolling = casinoRolling;
        this.slotRolling = slotRolling;
    }
}
