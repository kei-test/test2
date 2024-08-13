package GInternational.server.api.dto;

import GInternational.server.api.entity.Articles;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArticlesResponseDTO {

    private UserProfileDTO writer;
    private CategoryResponseDTO category;
    private Long id;
    private String title;
    private String content;
    private Boolean isTop;
    private String ownerName;
    private Integer readCount;
    private Integer commentCount;
    private Long previousArticleId;
    private Long nextArticleId;
    private boolean commentAllowed;
    private String site;

    private String ip;
    private String answerStatus;
    private String viewStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public ArticlesResponseDTO(Articles articles) {
        this.writer = new UserProfileDTO(articles.getWriter());
        this.category = new CategoryResponseDTO(articles.getCategory());
    }
}
