package GInternational.server.api.dto;

import GInternational.server.api.vo.AlarmEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AlarmReqDTO {

    private Long id;
    private AlarmEnum alarmEnum; // 알람이름
    private String sound; // 사운드(파일 url 경로, 클라우드에 저장됨)
}
