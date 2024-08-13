package GInternational.server.api.dto;

import GInternational.server.api.vo.TradeLogCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TradeLogResponseDTO {
    private Long id;
    private Long userId;

    private Long processedId; // 처리자
    private LocalDateTime processedAt; // 처리시간

    private String username; // 멤버Id
    private String nickName; // 닉네임
    private String role; // 멤버 구분

    private long firstAmazonMoney; // 변동전 머니
    private long amazonMoney; // 처리금액
    private long finalAmazonMoney; // 변동후 머니

    private long firstAmazonPoint; // 변동전 포인트
    private long amazonPoint; // 포인트
    private long finalAmazonPoint; // 최종 포인트

    private String bigo; // 비고

    private TradeLogCategory category; // 머니, 포인트
}
