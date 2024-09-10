package GInternational.server.amzn.dto.excclc;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmznPartnerCountDTO {

    private long dae;
    private long bon;
    private long bu;
    private long chong;
    private long mae;








    @QueryProjection
    public AmznPartnerCountDTO(long bon, long bu, long chong) {
        this.bon = bon;
        this.bu = bu;
        this.chong = chong;
    }
}
