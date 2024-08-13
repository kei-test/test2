package GInternational.server.api.dto;

import GInternational.server.api.entity.Comment;
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
public class CommentResDTO {
    private Long id;
    private String content;
    private UserProfileDTO writer;
    private List<CommentResDTO> children = new ArrayList<>();

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public CommentResDTO(Long id, String content, UserProfileDTO writer, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.writer = writer;
        this.createdAt = createdAt;
    }

    public static CommentResDTO convertCommentToDto(Comment comment) {
        return comment.isDeleted() ?
                new CommentResDTO(comment.getId(), "삭제된 댓글입니다.", null, null) :
                new CommentResDTO(comment.getId(), comment.getContent(),
                        new UserProfileDTO(comment.getWriter()), comment.getCreatedAt());
    }
}
