package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmazonMoneyResponseDTO {

    private long amazonMoney; //지급할 머니
    private String description; //사유
    private LocalDateTime createdAt; //머니 생성시간
}
