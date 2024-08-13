package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AmazonUserStatusEnum {

    WAITING("대기"),
    NORMAL("정상"),
    STOP("정지");

    private String value;
}

