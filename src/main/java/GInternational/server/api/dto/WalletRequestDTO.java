package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WalletRequestDTO {
    private Long userId;         // 회원 식별자
    private String ownerName;    // 예금주
    private String bankName;     // 은행명
    private long number;         // 계좌 번호
    private long numberPassword; // ?
    private long sportsBalance;  // 보유 머니
    private long point;          // 포인트
    private long casinoBalance;  // 카지노 머니
    private String bankPassword; // 환전비밀번호

    private long amazonMoney; // 총판페이지에서의 머니
    private long amazonPoint; // 총판페이지에서의 포인트
}
