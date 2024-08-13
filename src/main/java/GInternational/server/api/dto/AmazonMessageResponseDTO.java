package GInternational.server.api.dto;

import GInternational.server.api.entity.AmazonMessages;
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
public class AmazonMessageResponseDTO {
    private UserProfileDTO sender;
    private Long id;
    private String title;
    private String content;
    private boolean isRead;
    private UserProfileDTO receiver;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public AmazonMessageResponseDTO(AmazonMessages messages) {
        this.sender = new UserProfileDTO(messages.getSender());
    }
}
