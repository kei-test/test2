package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AmazonPointRequestDTO {

    private long amazonPoint; //지급,차감할 포인트
    private String description; //사유
}
