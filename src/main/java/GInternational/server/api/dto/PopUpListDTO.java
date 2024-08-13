package GInternational.server.api.dto;

import GInternational.server.api.vo.PopUpStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PopUpListDTO {

    private Long id;

    private String title;           // 제목
    private String content;         // 내용
    private int widthSize;          // 가로사이즈
    private int lengthSize;         // 세로사이즈
    private PopUpStatusEnum status; // 활성, 비활성
}
