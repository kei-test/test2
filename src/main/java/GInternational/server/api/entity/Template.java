package GInternational.server.api.entity;

import GInternational.server.api.vo.TemplateTypeEnum;
import GInternational.server.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "template")
public class Template extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private Long id;

    private int turn;       // 순번
    private String title;   // 제목
    private String content; // 내용

    private TemplateTypeEnum type; // CUSTOMER_CENTER("고객센터템플릿"), MONEY("머니템플릿"), POINT("포인트템플릿");
}
