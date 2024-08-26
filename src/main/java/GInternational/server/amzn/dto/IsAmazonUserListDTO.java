package GInternational.server.amzn.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class IsAmazonUserListDTO {

    private Long id;
    private String username;
    private String nickname;
    private String ownerName;
    private long sportsBalance;
    private long point;
    private long depositTotal;
    private long withdrawTotal;
    private long totalSettlement;
    private LocalDateTime createdAt;
    private LocalDateTime lastVisit;


    @QueryProjection
    public IsAmazonUserListDTO(Long id, String username, String nickname, String ownerName, long sportsBalance, long point, long depositTotal, long withdrawTotal, long totalSettlement, LocalDateTime createdAt, LocalDateTime lastVisit) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.ownerName = ownerName;
        this.sportsBalance = sportsBalance;
        this.point = point;
        this.depositTotal = depositTotal;
        this.withdrawTotal = withdrawTotal;
        this.totalSettlement = totalSettlement;
        this.createdAt = createdAt;
        this.lastVisit = lastVisit;
    }
}
