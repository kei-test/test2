package GInternational.server.l_sport.info.dto.count;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class PreMatchGameCountResponseDTO {
    private String sportsName;
    private Long count;


    @QueryProjection
    public PreMatchGameCountResponseDTO(String sportsName, Long count) {
        this.sportsName = sportsName;
        this.count = count;
    }
}
