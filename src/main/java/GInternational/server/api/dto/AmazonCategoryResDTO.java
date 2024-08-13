package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmazonCategoryResDTO {

    private Long id;
    @NotBlank(message = "카테고리 명칭을 입력해주세요.")
    private String name;
    private String categoryRole;

}
