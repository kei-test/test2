package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesRequestDTO {

    private String title;
    private String content;
    private Boolean isTop;
    private String viewStatus;
    private Integer readCount;
    private boolean commentAllowed = true;
}
