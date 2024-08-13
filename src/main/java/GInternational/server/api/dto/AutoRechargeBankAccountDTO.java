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
public class AutoRechargeBankAccountDTO {
    private Long id;
    private String bankName;
    private String number;
    private Boolean isUse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
