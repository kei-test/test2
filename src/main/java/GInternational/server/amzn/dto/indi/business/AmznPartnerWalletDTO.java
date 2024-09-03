package GInternational.server.amzn.dto.indi.business;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Setter
@Getter
public class AmznPartnerWalletDTO {

    private Long id;
    private String username;
    private long amazonMoney;
    private long amazonMileage;


    @QueryProjection
    public AmznPartnerWalletDTO(Long id, String username, long amazonMoney, long amazonMileage) {
        this.id = id;
        this.username = username;
        this.amazonMoney = amazonMoney;
        this.amazonMileage = amazonMileage;
    }
}
