package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MoneyLogCategoryEnum {

    충전("충전"),
    자동충전("자동충전"),
    환전("환전"),
    카지노머니로전환("카지노머니로전환"),
    스포츠머니로전환("스포츠머니로전환"),
    환전취소("환전취소"),
    베팅차감("베팅차감"),
    베팅취소("베팅취소"),
    베팅복구("베팅복구"),
    당첨("당첨"),
    보너스("보너스"),
    포인트전환("포인트전환"),
    머니수동지급("머니수동지급"),
    머니수동차감("머니수동차감"),
    머니쿠폰("머니쿠폰"),
    회수("회수");

    private String value;
}
