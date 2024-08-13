package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmazonCommentReqDTO {
    private Long userId;
    private Long parentId;
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
