package GInternational.server.kplay.debit.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DebitAmazonResponseDTO {
    private Long id;
    private String userName; // 유저 네임
    private String nickName; // 닉네임
    private int amount; // 금액
    private String prdName;
    private int prd_id; // 제품 ID
    private String txnId; // 고유 트랜잭션 ID
    private int creditAmount; // credit 테이블 amount
    private int game_id; // 게임 ID
    private String gameName; // 새로운 필드: 게임 이름
    private String table_id; // 테이블 ID
    private int credit_amount;
    private LocalDateTime created_at;
    private Long balance;
}
