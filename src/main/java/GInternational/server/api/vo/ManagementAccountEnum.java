package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ManagementAccountEnum {

    카지노캐시("카지노캐시"),
    앞방("앞방"),
    중간방("중간방"),
    뒷방("뒷방"),
    현금("현금");

    private String value;
}
