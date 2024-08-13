package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransformStatisticResDTO {

    private Map<Integer, Long> toCasino = new HashMap<>(); // 일별 카지노머니로 전환된 금액
    private Map<Integer, Integer> toCasinoCount = new HashMap<>(); // 일별 카지노머니로 전환된 건수
    private Map<Integer, String> toCasinoAverage = new HashMap<>(); // 일별 카지노머니로 전환된 평균금액 (toCasino 나누기 toCasinoCount)
    private Long totalToCasino; // 월 총 카지노머니로 전환된 금액 합계
    private Integer totalToCasinoCount; // 월 총 카지노머니로 전환된 건수 합계
    private String totalToCasinoAverage; // 월 총 카지노머니로 전환된 평균 금액 (monthlyToCasino 나누기 monthlyToCasinoCount)

    private Map<Integer, Long> toSports = new HashMap<>(); // 일별 스포츠머니로 전환된 금액
    private Map<Integer, Integer> toSportsCount = new HashMap<>(); // 일별 스포츠머니로 전환된 건수
    private Map<Integer, String> toSportsAverage = new HashMap<>(); // 일별 스포츠머니로 전환된 평균금액 (toSports 나누기 toSportsAverage)
    private Long totalToSports; // 월 총 스포츠머니로 전환된 금액 합계
    private Integer totalToSportsCount; // 월 총 스포츠머니로 전환된 건수 합계
    private String totalToSportsAverage; // 월 총 스포츠머니로 전환된 평균 금액 (monthlyToSports 나누기 monthlyToSportsCount)

    private Map<Integer, Long> toCasinoMinusToSports = new HashMap<>(); // 일별 카지노머니로 전환된 금액 - 스포츠머니로 전환된 금액 (toCasino - toSports)

    private String averageToCasino; // 월 평균 카지노머니로 전환된 금액 (totalToCasino 나누기 해당월의 일 수)
    private String averageToCasinoCount; // 월 평균 카지노머니로 전환된 횟수 (totalToCasinoCount 나누기 해당월의 일 수)
    private String averageToSports; // 월 평균 스포츠머니로 전환된 금액 (totalToSports 나누기 해당월의 일 수)
    private String averageToSportsCount; // 월 평균 스포츠머니로 전환된 횟수 (totalToSportsCount 나누기 해당월의 일 수)
    private Long totalToCasinoMinusToSports; // 각 일별 "카지노머니로 전환된 금액" - "스포츠머니로 전환된 금액"의 최종합계
    private Map<Integer, String> dailyProfitRate = new HashMap<>();  // 일별 수익률 추가
    private String monthlyAverageProfitRate;  // 월 평균 수익률 추가
}
