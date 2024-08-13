package GInternational.server.kplay.debit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebitRequestDTO {
    private int user_id; // AAS 사용자 ID
    private int amount; // 금액
    private int prd_id; // 제품 ID
    private String txn_id; // 고유 트랜잭션 ID
    private int game_id; // 게임 ID
    private String table_id; // 테이블 ID
    private int credit_amount; // credit_amount가 존재하는 경우
}