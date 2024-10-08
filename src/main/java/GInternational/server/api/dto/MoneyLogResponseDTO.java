package GInternational.server.api.dto;

import GInternational.server.api.vo.MoneyLogCategoryEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MoneyLogResponseDTO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;

    private Long usedSportsBalance; // 사용머니
    private Long finalSportsBalance; // 최종 스포츠머니
    private Long finalCasinoBalance; // 최종 카지노머니
    private String bigo; // 비고

    private String site; // 사이트

    private MoneyLogCategoryEnum category; // 충전, 환전, 베팅차감, 당첨, 포인트전환

    private Long totalBet; // 베팅차감 합계
    private Long totalWin; // 당첨 합계

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt; // 등록일시
}
