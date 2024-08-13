package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IpResDTO {
    private Long id;
    private String ipContent;
    private String note;
    private boolean enabled;
    private LocalDateTime createdAt;
}
