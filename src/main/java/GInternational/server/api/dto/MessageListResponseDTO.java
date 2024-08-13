package GInternational.server.api.dto;

import GInternational.server.api.entity.Messages;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class MessageListResponseDTO {
    private ManagerProfileDTO sender;
    private Long id;
    private String title;
    private boolean isRead;
    private boolean isPopup;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime readDate;

    public MessageListResponseDTO(Messages messages) {
        this.sender = new ManagerProfileDTO(messages.getSender());
    }

    @QueryProjection
    public MessageListResponseDTO(ManagerProfileDTO sender, Long id, String title, boolean isRead, LocalDateTime readDate, LocalDateTime createdAt) {
        this.sender = sender;
        this.id = id;
        this.title = title;
        this.isRead = isRead;
        this.readDate = readDate;
        this.createdAt = createdAt;
    }

    @QueryProjection
    public MessageListResponseDTO(ManagerProfileDTO sender, Long id, String title,LocalDateTime createdAt) {
        this.sender = sender;
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }
}
