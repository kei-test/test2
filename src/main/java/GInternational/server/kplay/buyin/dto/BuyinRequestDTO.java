package GInternational.server.kplay.buyin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyinRequestDTO {

    //일단 프론트가 서버에 주는 값인가? y

    //1. 먼저 프론트가 서버에서 값을 받을 때 사용하는 객체

    private int user_id;
    private int amount;
    private int prd_id;
    private String txn_id;
    private int game_id;
    private int credit_amount;
}
