package GInternational.server.api.dto;

import GInternational.server.api.entity.Messages;
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
public class MessageResponseDTO {
    private UserProfileDTO sender;
    private Long id;
    private String title;
    private String content;
    private boolean isRead;
    private UserProfileDTO receiver;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime readDate;

    public MessageResponseDTO(Messages messages) {
        this.sender = new UserProfileDTO(messages.getSender());
    }
}
