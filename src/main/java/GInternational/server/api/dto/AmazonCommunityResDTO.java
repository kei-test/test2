package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonCommunity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AmazonCommunityResDTO {

    private AmazonCategoryReqDTO category;
    private Long id;
    private Long parentId;
    private String title;
    private String content;
    private String status;
    private Boolean isTop;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    public AmazonCommunityResDTO(AmazonCommunity amazonCommunity) {
        this.category = new AmazonCategoryReqDTO(amazonCommunity.getAmazonCategory());
        this.id = amazonCommunity.getId();
        this.parentId = (amazonCommunity.getParent() != null) ? amazonCommunity.getParent().getId() : null;
        this.title = amazonCommunity.getTitle();
        this.content = amazonCommunity.getContent();
        this.status = amazonCommunity.getStatus();
        this.isTop = amazonCommunity.getIsTop();
        this.createdAt = amazonCommunity.getCreatedAt();
        this.updatedAt = amazonCommunity.getUpdatedAt();
    }
}
