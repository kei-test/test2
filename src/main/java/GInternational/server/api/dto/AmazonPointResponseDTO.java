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
public class AmazonPointResponseDTO {

    private long amazonPoint; //지급할 포인트
    private String description; //사유
    private LocalDateTime createdAt; //포인트 생성시간
}
