package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AppType {

    DEPOSIT("입금"),
    WITHDRAW("출금");

    private String value;
}