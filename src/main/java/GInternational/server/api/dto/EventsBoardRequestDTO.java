package GInternational.server.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventsBoardRequestDTO {

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private boolean enabled;

    private String viewStatus;
    private int readCount;
}
