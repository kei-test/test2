package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonComment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AmazonCommentResDTO {
    private Long id;
    private String title;
    private String content;
    private String type;
    private String status;
    private UserProfileDTO writer;
    private List<AmazonCommentResDTO> children = new ArrayList<>();

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public AmazonCommentResDTO(Long id, String content, UserProfileDTO writer, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public AmazonCommentResDTO(AmazonComment amazonComment) {
        this.id = amazonComment.getId();
        this.title = amazonComment.getTitle();
        this.content = amazonComment.getContent();
        this.type = amazonComment.getType();
        this.status = amazonComment.getStatus();
        this.writer = new UserProfileDTO(amazonComment.getWriter());
        this.createdAt = amazonComment.getCreatedAt();
}

    public static AmazonCommentResDTO convertCommentToDto(AmazonComment comment) {
        return comment.isDeleted() ?
                new AmazonCommentResDTO(comment.getId(), "삭제된 댓글입니다.", null, null) :
                new AmazonCommentResDTO(comment.getId(), comment.getContent(),
                        new UserProfileDTO(comment.getWriter()), comment.getCreatedAt());
    }
}
