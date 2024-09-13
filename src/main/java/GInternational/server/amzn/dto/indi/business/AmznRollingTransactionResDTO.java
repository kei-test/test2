package GInternational.server.amzn.dto.indi.business;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmznRollingTransactionResDTO {


    private Long id;
    private String category;
    private Long betUserId;
    private String username;
    private String nickname;
    private int betAmount;
    private long rollingAmount;
    private long totalBetAmount;
    private long totalRollingAmount;
    private LocalDateTime betTime;
    private LocalDateTime processedAt;

    @QueryProjection
    public AmznRollingTransactionResDTO(Long id, String category, String username, String nickname, int betAmount, long rollingAmount, LocalDateTime betTime, LocalDateTime processedAt) {
        this.id = id;
        this.category = category;
        this.username = username;
        this.nickname = nickname;
        this.betAmount = betAmount;
        this.rollingAmount = rollingAmount;
        this.betTime = betTime;
        this.processedAt = processedAt;
    }
}
