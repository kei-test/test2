package GInternational.server.kplay.balance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CasinoBalanceResponseDTO {
    private int status;     // 처리상태
    private long balance;    // 유저의 잔고
    private String error;   // 에러메세지


    public CasinoBalanceResponseDTO(int status, long balance ) {
        this.status = status;
        this.balance = balance;
    }

    public CasinoBalanceResponseDTO(int status, String error) {
        this.status = status;
        this.error = error;
    }


    public static CasinoBalanceResponseDTO createFailureResponse(String error) {
        return new CasinoBalanceResponseDTO(0,error);
    }
}