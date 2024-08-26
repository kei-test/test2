package GInternational.server.amzn.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AmznPartnerObjectDTO {

    private Long id;
    private String username;
    private String partnerType;
    private Long daeId;
    private Long bonId;
    private Long buId;
    private Long chongId;
    private Long maeId;


    @QueryProjection
    public AmznPartnerObjectDTO(Long id, String username, String partnerType, Long daeId, Long bonId, Long buId, Long chongId, Long maeId) {
        this.id = id;
        this.username = username;
        this.partnerType = partnerType;
        this.daeId = daeId;
        this.bonId = bonId;
        this.buId = buId;
        this.chongId = chongId;
        this.maeId = maeId;
    }
}
