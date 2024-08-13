package GInternational.server.api.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonthlyBetStatisticDailyAmountDTO {

    private int day;
    private Long rechargeAmount;
    private Long exchangeAmount;
    private Long debitAmount;
    private Long point;
    private Long customerCenterPosts; // 고객센터 게시물 수
    private Long articlePosts; // 게시판 게시물 수
    private Long join; // 회원가입 수
    private Long rechargeCount; // 실입금자 수

    public MonthlyBetStatisticDailyAmountDTO(
            int day,
            Long rechargeAmount,
            Long exchangeAmount,
            Long debitAmount,
            Long point,
            Long customerCenterPosts,
            Long articlePosts,
            Long join,
            Long rechargeCount) {
        this.day = day;
        this.rechargeAmount = rechargeAmount;
        this.exchangeAmount = exchangeAmount;
        this.debitAmount = debitAmount;
        this.point = point;
        this.customerCenterPosts = customerCenterPosts;
        this.articlePosts = articlePosts;
        this.join = join;
        this.rechargeCount = rechargeCount;
    }
}
