package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatusEnum {

    꽝("꽝"),
    지급완료("지급완료"),
    지급대기중("지급대기중");

    private String value;

    public static PaymentStatusEnum fromString(String text) {
        for (PaymentStatusEnum b : PaymentStatusEnum.values()) {
            if (b.value.equalsIgnoreCase(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}
