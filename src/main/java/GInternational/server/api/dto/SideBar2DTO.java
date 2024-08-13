package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SideBar2DTO {
    private Long totalSportsBalance;            // "전체회원 보유금액", 모든 유저의 SportsBalance 합계
    private Long totalPoint;                    // "전체회원 보유포인트", 모든 유저의 point 합계
    private Integer todayRechargeCount;         // "충전금", 충전건수 합계와 충전금액 합계 (당일), 충전금(건수) : (합계)원 형식으로 표시되어야 함
    private Long todayRechargeSum;              // "충전금", 충전건수 합계와 충전금액 합계 (당일), 충전금(건수) : (합계)원 형식으로 표시되어야 함
    private Integer todayExchangeCount;         // "환전금", 환전건수 합계와 환전금액 합계 (당일), 환전금(건수) : (합계)원 형식으로 표시되어야 함
    private Long todayExchangeSum;              // "환전금", 환전건수 합계와 환전금액 합계 (당일), 환전금(건수) : (합계)원 형식으로 표시되어야 함
    private Long todayDifference;               // "충전금 - 환전금", 당일 충전금액에서 당일 환전금액을 뺀 금액

    private int todayTransformToCasinoCount;    // "카지노충전 건수"
    private Long todayTransformToCasinoBalance; // "카지노충전금", 당일 스포츠머니에서 카지노머니로 전환된 금액 합계
    private int todayTransformToSportsCount;    // "카지노환전 건수"
    private Long todayTransformToSportsBalance; // "카지노환전금", 당일 카지노머니에서 스포츠머니로 전환된 금액 합계

    private Long todayPoint;           // "수동지급 포인트", 당일 수동으로 지급된 포인트 합계 (포인트로그의 PointLogCategoryEnum category값이 포인트수동지급인 값 합계)
    private Long todayAutoPoint;       // "자동지급 포인트", 당일 자동으로 지급된 포인트 합계 (포인트로그의 PointLogCategoryEnum category값이 룰렛, 충전, 출석체크룰렛, 슬롯롤링적립, 사과줍기, 행운복권, 콤프인 값 합계)
    private Long todayChargedCountSum; // "금일 첫충전 회원", 금일 충전을한 유저의 합계 (wallet의 todayChargedCount가 1인 유저들의 합계(todayChargedCount는 1또는 0임), 몇명인지)
    private int todayJoinSum;          // "금일 가입 회원", 금일 회원가입한 유저의 합계 (user엔티티에서 createdAt을 당일 기준으로 필터링하여 합계)
}