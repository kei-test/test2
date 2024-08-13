package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointLogCategoryEnum {

    룰렛("룰렛"),
    충전("충전"),
    자동충전("자동충전"),
    포인트전환("포인트전환"),
    출석체크룰렛("출석체크룰렛"),
    만근출석보상("만근출석보상"),
    일일출석보상("일일출석보상"),
    슬롯롤링적립("슬롯롤링적립"),
    사과줍기("사과줍기"),
    행운복권("행운복권"),
    포인트수동지급("포인트수동지급"),
    포인트수동차감("포인트수동차감"),
    콤프("콤프"),
    추천인낙첨포인트("추천인낙첨포인트"),
    낙첨포인트("낙첨포인트");

    private String value;

    // 추가된 메서드
    public PointLogCategoryEnum getValue() {
        return PointLogCategoryEnum.valueOf(value);
    }
}
