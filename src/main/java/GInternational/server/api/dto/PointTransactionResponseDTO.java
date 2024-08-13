package GInternational.server.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PointTransactionResponseDTO {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime processedAt;  //처리 시간
    private String description;  //내용
    private int usedPoint;  //전환에 사용한 포인트
    private int remainingPoint;  //전환 후 보유 포인트
    private String ip;

}
