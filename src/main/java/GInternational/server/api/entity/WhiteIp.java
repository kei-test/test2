package GInternational.server.api.entity;

import GInternational.server.api.vo.WhiteIpMemoStatusEnum;
import GInternational.server.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "white_ip")
public class WhiteIp extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "white_ip_id")
    private Long id;
    @Column(name = "white_ip", unique = true)
    private String whiteIp;
    @Column(name = "memo")
    private String memo;
    @Column(name = "memo_status")
    private WhiteIpMemoStatusEnum memoStatus;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
