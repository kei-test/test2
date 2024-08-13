package GInternational.server.api.entity;

import GInternational.server.api.vo.AlarmEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "alarm")
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private AlarmEnum alarmEnum; // 알람이름
    private String sound; // 사운드(파일 url 경로, 클라우드에 저장됨)
}
