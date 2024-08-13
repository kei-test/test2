package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditInfoDTO {
    private String targetId;
    private String username;
    private String ip;
    private String details;
    private String adminUsername;
}
