package GInternational.server.api.dto;

import GInternational.server.api.entity.EventsBoard;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsBoardResponseDTO {

    private UserProfileDTO writer;
    private Long id;
    private String title;
    private boolean enabled;
    private String description;
    private String viewStatus;
    private int readCount;
    private int commentCount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;


    public EventsBoardResponseDTO(EventsBoard eventsBoard) {
        this.writer = new UserProfileDTO(eventsBoard.getWriter());
        this.id = eventsBoard.getId();
        this.title = eventsBoard.getTitle();
        this.enabled = eventsBoard.isEnabled();
        this.description = eventsBoard.getDescription();
        this.viewStatus = eventsBoard.getViewStatus();
        this.readCount = eventsBoard.getReadCount();
        this.commentCount = eventsBoard.getCommentCount();
        this.startDate = eventsBoard.getStartDate();
        this.endDate = eventsBoard.getEndDate();
    }
}
