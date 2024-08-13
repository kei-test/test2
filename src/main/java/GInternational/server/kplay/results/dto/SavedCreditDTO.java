package GInternational.server.kplay.results.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SavedCreditDTO {

    //크레딧에 저장시킬 때 프론트에서 주는 값을 셋팅하는 DTO
    private int prdId;
    private String txnId;
    private int type;
    private int gameId;
    private int stake;
    private int payout;
    private int is_cancel;
    private String error;

}
