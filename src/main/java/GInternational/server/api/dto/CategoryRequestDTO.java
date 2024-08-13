package GInternational.server.api.dto;

import GInternational.server.api.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDTO {

    @NotBlank(message = "카테고리 명칭을 입력해주세요.")
    private String name;
    private String categoryRole;

    public CategoryRequestDTO(Category category) {
        this.name = category.getName();
    }
}
