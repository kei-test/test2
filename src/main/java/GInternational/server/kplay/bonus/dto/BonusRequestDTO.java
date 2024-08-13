package GInternational.server.kplay.bonus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BonusRequestDTO {
    private int user_id;   // AAS 사용자 ID
    private int type;
    //    Type 0: 인 게임 보너스
    //    Type 1: 프로모션
    //    Type 2: 잭팟
    private int amount;    // 금액
    private int prd_id;    // 제품 ID
    private int game_id;   // 게임 ID
    private String txn_id; // 테이블 ID
}