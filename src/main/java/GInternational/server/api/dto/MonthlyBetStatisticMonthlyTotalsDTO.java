package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyBetStatisticMonthlyTotalsDTO {

    private Map<Integer, Long> dailyRechargeTotals; // 각 일별 충전
    private Map<Integer, Long> dailyExchangeTotals; // 각 일별 환전
    private Map<Integer, Long> dailyDifferenceAmounts; // 각 일별 차액
    private Map<Integer, Double> dailyRevenueRates; // 각 일별 수익률
    private Map<Integer, Long> dailyDebitTotals; // 각 일별 베팅
    private Map<Integer, Long> dailyBetRewardTotals; // 각 일별 당첨금
    private Map<Integer, Long> dailyPointTotals; // 각 일별 지급 포인트
    private Map<Integer, Long> dailyCustomerCenterPosts; // 각 일별 고객센터 게시물
    private Map<Integer, Long> dailyArticlePosts; // 각 일별 게시판 게시물
    private Map<Integer, Long> dailyJoinTotals; // 각 일별 회원가입
    private Map<Integer, Long> dailyRechargeCounts; // 각 일별 실입금자

    private Long monthlyRechargeTotal; // 월 총 충전
    private Long monthlyExchangeTotal; // 월 총 환전
    private Long monthlyDifferenceAmount; // 월 총 차액
    private Double monthlyRevenueRate; // 월 총 수익률
    private Long monthlyDebitTotal; // 월 총 베팅
    private Long monthlyBetRewardTotal; // 월 총 당첨금
    private Long monthlyPointTotal; // 월 총 지급 포인트
    private Long monthlyCustomerCenterPostsTotal; // 월 총 고객센터 게시물
    private Long monthlyArticlePostsTotal; // 월 총 게시판 게시물
    private Long monthlyJoinTotal; // 월 총 회원가입
    private Long monthlyRechargeCountTotal; // 월 총 실입금자
}