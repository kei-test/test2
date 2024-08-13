package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmEnum {

    충전요청("충전요청"),
    환전요청("환전요청"),
    가입신청("가입신청"),
    고객센터("고객센터"),
    비번찾기문의("비번찾기문의"),
    비번변경문의("비번변경문의"),
    베팅모니터링("베팅모니터링");

    private String value;
}
