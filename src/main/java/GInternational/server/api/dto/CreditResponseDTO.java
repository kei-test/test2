package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditResponseDTO {
    private int status;  // 처리상태
    private long balance; // 유저의 잔고
    private String error;


    public CreditResponseDTO(int status, long balance) {
        this.status = status;
        this.balance = balance;
    }


    public CreditResponseDTO(int status,String error) {
        this.status = status;
        this.error = error;
    }

    public static CreditResponseDTO createFailureResponse(String error) {
        return new CreditResponseDTO(0,error);
    }
}
