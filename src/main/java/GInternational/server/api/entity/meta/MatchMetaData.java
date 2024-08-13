package GInternational.server.api.entity.meta;

import GInternational.server.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "match_meta")
public class MatchMetaData extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "match_id", length = 50, nullable = false)
    private String matchId;

    @Column(name = "pre_count", nullable = false,columnDefinition = "int default 0")
    private int preCount;

    @Column(name = "live_count", nullable = false,columnDefinition = "int default 0")
    private int liveCount;

    @Column(name = "pre_total_amount", length = 13, nullable = false,columnDefinition = "varchar(13)  default '0'")
    private String preTotalAmount;

    @Column(name = "live_total_amount", length = 13, nullable = false,columnDefinition = "varchar(13)  default '0'")
    private String liveTotalAmount;
}
