package GInternational.server.kplay.debit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebitResponseDTO {
    private int status;     // 처리상태
    private long balance; // 유저의 잔고
    private String error;

    public DebitResponseDTO(int status, String error) {
        this.status = status;
        this.error = error;
    }

    public DebitResponseDTO(int status, long balance) {
        this.status = status;
        this.balance = balance;
    }

    public static DebitResponseDTO createFailureResponse(String error) {
        return new DebitResponseDTO(0,error);
    }
}