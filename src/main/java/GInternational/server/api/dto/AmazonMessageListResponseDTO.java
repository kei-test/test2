package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonMessages;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class AmazonMessageListResponseDTO {
    private AmazonUserManagerProfileDTO sender;
    private Long id;
    private String title;
    private boolean isRead;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public AmazonMessageListResponseDTO(AmazonMessages messages) {
        this.sender = new AmazonUserManagerProfileDTO(messages.getSender());
    }

    @QueryProjection
    public AmazonMessageListResponseDTO(AmazonUserManagerProfileDTO sender, Long id, String title, boolean isRead, LocalDateTime createdAt) {
        this.sender = sender;
        this.id = id;
        this.title = title;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public AmazonMessageListResponseDTO(AmazonUserManagerProfileDTO sender, Long id, String title, LocalDateTime createdAt) {
        this.sender = sender;
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }
}
