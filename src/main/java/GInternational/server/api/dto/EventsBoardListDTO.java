package GInternational.server.api.dto;

import GInternational.server.api.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventsBoardListDTO {
    private Long id;

    private Long userId;
    private String writerUsername;
    private String site;
    private Integer readCount;
    private Integer commentCount;
    private String title;
    private String description;
    private boolean enabled;
    private String viewStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime endDate;
}
