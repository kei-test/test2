package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserMonitoringStatusEnum {

    정상("정상"),
    초과베팅("초과베팅"),
    주시베팅("주시베팅");

    private String value;
}
