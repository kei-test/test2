package GInternational.server.amzn.dto.indi.indi_prj;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.checkerframework.checker.units.qual.A;


@NoArgsConstructor
@Setter
@Getter
public class AmznRollingDTO {

    private Long id;
    private String username;
    private String category;
    private long rollingAmount;





    @QueryProjection
    public AmznRollingDTO(Long id, String username, String category, long rollingAmount) {
        this.id = id;
        this.username = username;
        this.category = category;
        this.rollingAmount = rollingAmount;
    }
}
