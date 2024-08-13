package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppStatus {

    OK("승인 완료"),
    CANCEL("취소"),
    TIMEOUT("시간 초과"),
    WAIT("대기");


    private String value;
}