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
public class AutoRechargePhoneDTO {
    private Long id;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
