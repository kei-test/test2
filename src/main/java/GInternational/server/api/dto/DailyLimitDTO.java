package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyLimitDTO {

    private Long id;
    private int dailyArticleLimit;
    private int dailyCommentLimit;
    private int dailyArticlePoint;
    private int dailyCommentPoint;
}
