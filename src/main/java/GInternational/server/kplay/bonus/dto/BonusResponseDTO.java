package GInternational.server.kplay.bonus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BonusResponseDTO {
    private int status;      // 처리상태
    private long balance;    // 유저의 잔고
    private String error;    // 에러메세지


    public BonusResponseDTO(int status, long balance) {
        this.status = status;
        this.balance = balance;
    }

    public BonusResponseDTO(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public static BonusResponseDTO createFailureResponse(String error) {
        return new BonusResponseDTO(0,error);
    }
}