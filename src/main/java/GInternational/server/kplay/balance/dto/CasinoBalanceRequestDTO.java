package GInternational.server.kplay.balance.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CasinoBalanceRequestDTO {
    private int user_id; // AAS 사용자 ID
    private int prd_id; // 제품 ID
}