package GInternational.server.l_sport.info.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "api_live_score_prematch", indexes = {
        @Index(name = "idx", columnList = "idx"),
        @Index(name = "match_id", columnList = "match_id")})
public class LivescorePrematch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private Long idx;

    @Column(name = "match_id", nullable = false, columnDefinition = "varchar(50)")
    private String matchId;

    @Column(name = "status")
    private String status;

    @Column(name = "time_v")
    private String timeV;

    @Column(name = "current_period")
    private String currentPeriod;

    @Column(name = "home_score")
    private String homeScore;

    @Column(name = "away_score")
    private String awayScore;

    @Column(name = "update_dttm", nullable = false, columnDefinition = "datetime default CURRENT_TIMESTAMP", updatable = false, insertable = false)
    private LocalDateTime updateDttm;
}
