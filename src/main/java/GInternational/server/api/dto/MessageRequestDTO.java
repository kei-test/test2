package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequestDTO {
    private Long receiverId;
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private boolean isPopup;
}
