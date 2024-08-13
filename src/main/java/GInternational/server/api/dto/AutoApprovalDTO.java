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
public class AutoApprovalDTO {

    private Long amount;
    private String bank;
    private String owner;
    private String message;
    private LocalDateTime depositTime;
    private LocalDateTime receptionTime;

}
