package GInternational.server.amzn.dto.index;

import GInternational.server.api.vo.UserGubunEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznUserDetailDTO {


    private UserGubunEnum gubun;
    private int lv;
    private String password;
    private String phone;
    private String ownerName;
    private String bankName;
    private Long number;
    private String referredBy;


    @QueryProjection
    public AmznUserDetailDTO(UserGubunEnum gubun, int lv, String password, String phone, String ownerName, String bankName, Long number, String referredBy) {
        this.gubun = gubun;
        this.lv = lv;
        this.password = password;
        this.phone = phone;
        this.ownerName = ownerName;
        this.bankName = bankName;
        this.number = number;
        this.referredBy = referredBy;
    }
}
