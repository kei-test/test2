package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdjustPointRequestDTO {
    private boolean isAddition; // true면 포인트 지급, false면 차감
    private long point; // 지급 또는 차감할 포인트 양
    private String bigo; // 비고
}
