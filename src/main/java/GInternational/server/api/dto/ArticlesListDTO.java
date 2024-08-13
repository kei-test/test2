package GInternational.server.api.dto;

import GInternational.server.api.entity.Articles;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesListDTO {
    private UserProfileDTO writer;
    private CategoryRequestDTO category;
    private Long id;
    private Boolean isTop;
    private String title;
    private String content;
    private String viewStatus;
    private boolean commentAllowed;
    private String site;
    private String ip;

    private int readCount; // 조회수
    private int commentCount; // 댓글수
    private String answerStatus; // 답변상태

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public ArticlesListDTO(Articles articles) {
        this.writer = new UserProfileDTO(articles.getWriter());
        this.category = new CategoryRequestDTO(articles.getCategory());
    }
}
