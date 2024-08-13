package GInternational.server.partner.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PartnerCountDTO {

    private Long daeId; // 대본사
    private Long bonId; // 본사
    private Long buId; // 부본사
    private Long chongId; // 총판
    private Long maeId; //매장
    private String partnerType; // 타입별 카운트


    @QueryProjection
    public PartnerCountDTO(Long daeId, Long bonId, Long buId, Long chongId, Long maeId) {
        this.daeId = daeId;
        this.bonId = bonId;
        this.buId = buId;
        this.chongId = chongId;
        this.maeId = maeId;
    }


    @QueryProjection
    public PartnerCountDTO(Long daeId, Long bonId, Long buId, Long chongId) {
        this.daeId = daeId;
        this.bonId = bonId;
        this.buId = buId;
        this.chongId = chongId;
    }
}
