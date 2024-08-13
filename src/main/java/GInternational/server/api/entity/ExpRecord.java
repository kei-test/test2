package GInternational.server.api.entity;

import GInternational.server.api.vo.ExpRecordEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "exp_record")
public class ExpRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exp_record_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;
    private String username;
    private String nickname;
    private long exp;
    private String ip;

    @Enumerated(EnumType.STRING)
    private ExpRecordEnum content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
