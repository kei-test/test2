package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AmazonCategoryReqDTO {

    @NotBlank(message = "카테고리 명칭을 입력해주세요.")
    private String name;
    private String categoryRole;

    public AmazonCategoryReqDTO(AmazonCategory amazonCategory) {
        this.name = amazonCategory.getName();
        this.categoryRole = amazonCategory.getCategoryRole();
    }
}
