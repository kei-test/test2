package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EditPreMatchDataList {

    private String matchId;
    private String marketName;
    private String homeScore;
    private String awayScore;
    private String winOrOverIdx;
    private String winOrOverSettlement;
    private String drawOrBaseLineIdx;
    private String drawOrBaseLineSettlement;
    private String loseOrUnderIdx;
    private String loseOrUnderSettlement;
    private String status;
}
