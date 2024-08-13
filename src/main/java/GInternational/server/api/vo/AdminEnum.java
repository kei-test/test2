package GInternational.server.api.vo;

import lombok.Getter;

@Getter
public enum AdminEnum {
    사용중("사용중"),
    사용불가("사용불가");

    private String 표시이름;

    AdminEnum(String 표시이름) {
        this.표시이름 = 표시이름;
    }
}
