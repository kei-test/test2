package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WhiteIpMemoStatusEnum {

    CONFIGURED("설정"),
    NOT_CONFIGURED("미설정");

    private final String value;
}
