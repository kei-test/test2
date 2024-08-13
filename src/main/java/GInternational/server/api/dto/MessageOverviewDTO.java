package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageOverviewDTO {
    private Long id;
    private String title;
    private String site;
    private String sender; // sender의 username
    private LocalDateTime createdAt;
    private LocalDateTime readDate;
    private boolean isRead;
    private boolean deletedBySender;
    private boolean deletedByReceiver;
    private String receiverUsername;
    private String receiverNickname;
}
