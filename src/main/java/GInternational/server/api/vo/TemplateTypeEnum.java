package GInternational.server.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemplateTypeEnum {

    CUSTOMER_CENTER("고객센터템플릿"),
    MONEY("머니템플릿"),
    POINT("포인트템플릿");

    private String value;
}
