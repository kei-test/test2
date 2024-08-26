package GInternational.server.amzn.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AmznDetailsByTypeDTO {

    private String partnerType;
    private String username;
    private String nickname;
    private String password;
    private String phone;
    private double casinoRolling;
    private double slotRolling;


    @QueryProjection
    public AmznDetailsByTypeDTO(String partnerType, String username, String nickname, String password, String phone, double casinoRolling, double slotRolling) {
        this.partnerType = partnerType;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.phone = phone;
        this.casinoRolling = casinoRolling;
        this.slotRolling = slotRolling;
    }
}
