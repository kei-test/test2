package GInternational.server.api.dto;

import GInternational.server.api.vo.TemplateTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRequestDTO {
    private Long id;
    private int turn;       // 순번
    private String title;   // 제목
    private String content; // 내용

    private TemplateTypeEnum type; // CUSTOMER_CENTER("고객센터템플릿"), MONEY("머니템플릿"), POINT("포인트템플릿");
}
