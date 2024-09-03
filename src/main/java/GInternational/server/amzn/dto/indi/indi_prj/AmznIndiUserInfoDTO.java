package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class AmznIndiUserInfoDTO {

    private Long id;
    private String username;
    private String nickname;
    private Integer aasId;
    private String referredBy;
    private String partnerType;
    private boolean isAmazonUsers;


    @QueryProjection
    public AmznIndiUserInfoDTO(Long id, String username, String nickname, Integer aasId, String referredBy, String partnerType, boolean isAmazonUsers) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.aasId = aasId;
        this.referredBy = referredBy;
        this.partnerType = partnerType;
        this.isAmazonUsers = isAmazonUsers;
    }
}
