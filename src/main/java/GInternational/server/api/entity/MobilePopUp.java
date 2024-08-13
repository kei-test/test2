package GInternational.server.api.entity;

import GInternational.server.api.vo.PopUpStatusEnum;
import GInternational.server.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Setter
@Getter
@Entity(name = "mobile_pop_up")
public class MobilePopUp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mobile_pop_up_id")
    private Long id;

    private String title;           // 제목
    private String content;         // 내용
    @Column(name = "width_size")
    private int widthSize;          // 가로사이즈
    @Column(name = "length_size")
    private int lengthSize;         // 세로사이즈
    @Column(name = "top_position")
    private int topPosition;        // 노출위치 top
    @Column(name = "left_position")
    private int leftPosition;       // 노출위치 left
    @Enumerated(EnumType.STRING)
    private PopUpStatusEnum status; // 활성, 비활성
    @Column(name = "priority_number")
    private int priorityNumber;     // 팝업 순위 (높은숫자가 위에 팝업됨)
}
