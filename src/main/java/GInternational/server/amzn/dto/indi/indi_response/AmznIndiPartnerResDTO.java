package GInternational.server.amzn.dto.indi.indi_response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmznIndiPartnerResDTO {

    private Long id;
    private String partnerType;
    private String username;
    private String nickname;
    private String referredBy;
    private int aas;
    private long amazonMoney;
    private long amazonMileage;
    private long amazonRollingAmount;
    private long rechargeAmount;
    private long exchangeAmount;
    private long totalSettlement;

    private AmznUserResDTO users = new AmznUserResDTO();
    private AmznKplayResDTO slot = new AmznKplayResDTO();
    private AmznKplayResDTO scnSlot = new AmznKplayResDTO();
    private AmznKplayResDTO casino = new AmznKplayResDTO();
    private AmznSportResDTO sport = new AmznSportResDTO();
    private AmznKplayResDTO arcade = new AmznKplayResDTO();



    @QueryProjection
    public AmznIndiPartnerResDTO(Long id, String partnerType, String username, String nickname,String referredBy,int aas, long amazonMoney, long amazonMileage) {
        this.id = id;
        this.partnerType = partnerType;
        this.username = username;
        this.nickname = nickname;
        this.referredBy = referredBy;
        this.aas = aas;
        this.amazonMoney = amazonMoney;
        this.amazonMileage = amazonMileage;
    }
}
