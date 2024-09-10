package GInternational.server.amzn.dto.excclc;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AdminExcclcCalculateDTO {


    private long todayTotalDeposit;
    private long todayTotalExchange;
    private long todayTotalSettlement;
    private long totalSportsBalance;
    private long betweenUserCount;
    private long betweenUserTotalDeposit;
    private long betweenUserTotalExchange;


    @QueryProjection
    public AdminExcclcCalculateDTO(long todayTotalDeposit, long todayTotalExchange, long todayTotalSettlement, long totalSportsBalance) {
        this.todayTotalDeposit = todayTotalDeposit;
        this.todayTotalExchange = todayTotalExchange;
        this.todayTotalSettlement = todayTotalSettlement;
        this.totalSportsBalance = totalSportsBalance;
    }

    @QueryProjection
    public AdminExcclcCalculateDTO(long betweenUserCount) {
        this.betweenUserCount = betweenUserCount;
    }
}
