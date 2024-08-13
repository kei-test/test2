package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SideBar3DTO {
    private String todayBetAmountSum; // "금일베팅금액", 스포츠 베팅 금액, betHistory에서 OrderStatusEnum orderStatus값이 WAITING("대기"), HIT("적중"), FAIL("낙첨")인 값들의 bet(String) 합계
                                      // (betGroupId가 같은 베팅건이 있다면 하나의 bet만 계산해야 함)
    private String todayAdjustment;   // "금일정산금액", 총베팅금액에서 총당첨금액을 뺀 금액 (betGroupId가 같은 베팅건이 있다면 하나의 bet만 계산해야 함)
    private Integer prematchCount;    // "스포츠 프리매치(0)", 프리매치 베팅건수 합계, ()안에 표기 (당일)
    private String prematchSum;       // "스포츠 프리매치(0)", 프리매치 베팅금액 합계 (당일)
    private Integer inplayCount;      // "스포츠 실시간(0)", 인플레이 베팅건수 합계, ()안에 표기 (당일)
    private String inplaySum;         // "스포츠 실시간(0)", 인플레이 베팅금액 합계 (당일)
    private Integer normalCount;      // "스포츠 일반(0)", 일반 베팅건수 합계, ()안에 표기 (당일)
    private String normalSum;         // "스포츠 일반(0)", 일반 베팅금액 합계 (당일)
    private Integer specialCount;     // "스포츠 스페셜(0)", 스페셜 베팅건수 합계, ()안에 표기 (당일)
    private String specialSum;        // "스포츠 스페셜(0)", 스페셜 베팅금액 합계 (당일)
    private Integer special2Count;    // "스포츠 스페셜2(0)", 스페셜2 베팅건수 합계, ()안에 표기 (당일)
    private String special2Sum;       // "스포츠 스페셜2(0)", 스페셜2 베팅금액 합계 (당일)
    private String leftBet;           // "남은베팅(합산)", 대기중인 베팅건의 베팅금액 합산 (betGroupId가 같은 베팅건이 있다면 하나의 bet만 계산해야 함)
}
