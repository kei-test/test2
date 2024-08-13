package GInternational.server.api.dto;

import GInternational.server.api.vo.PopUpStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PopUpResponseDTO {

    private Long id;
    private String title;            // 제목
    private String content;          // 내용
    private int widthSize;           // 가로사이즈
    private int lengthSize;          // 세로사이즈
    private int topPosition;         // 노출위치 top
    private int leftPosition;        // 노출위치 left
    private PopUpStatusEnum status;  // 활성, 비활성
    private int priorityNumber;      // 팝업 순위 (높은숫자가 위에 팝업됨)
    private LocalDateTime createdAt; // 팝업 생성 시간
    private LocalDateTime updatedAt; // 팝업 업데이트 시간
}
