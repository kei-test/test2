package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AutoRechargeDTO {
    private Long id;
    private Long userId;
    private String username;
    private String site;
    private Long number;
    private String bankName;
    private String ownerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime timestamp;
    private String message;
    private String depositor;
    private String amount;
    private String status;
}
