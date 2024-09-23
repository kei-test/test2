package GInternational.server.amzn.dto.total;

import GInternational.server.api.vo.UserGubunEnum;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznDaeOrDSTResDTO {

    private Long userId;
    private String username;
    private String nickname;
    private double totalRolling;
    private double casinoRolling;
    private double slotRolling;
    private int totalUserCount;
    private UserGubunEnum userGubunEnum;
    private String partnerType;

    private String type;
    private String referredBy;
    private long wait;
    private long normal;
    private long singleF;
    private long odd;
    private long howon;
    private long bad;
    private long malice;
    private long stop;
    private long reject;
    private long downwardWithdrawal;
    private long withdraw1;
    private long withdraw2;
    private long withdraw3;



    @QueryProjection
    public AmznDaeOrDSTResDTO(Long userId, String username, String nickname, double casinoRolling, double slotRolling, int totalUserCount, UserGubunEnum userGubunEnum, String partnerType) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.casinoRolling = casinoRolling;
        this.slotRolling = slotRolling;
        this.totalUserCount = totalUserCount;
        this.userGubunEnum = userGubunEnum;
        this.partnerType = partnerType;
    }


    @QueryProjection
    public AmznDaeOrDSTResDTO(String referredBy, UserGubunEnum userGubunEnum) {
        this.referredBy = referredBy;
        this.userGubunEnum = userGubunEnum;
    }
}
