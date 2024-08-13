package GInternational.server.api.entity.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "odd_meta")
public class OddMetaData {


    @Id
    @Column(name = "idx",length = 50,nullable = false)
    private String idx;

    @Column(name = "match_id",length = 50,nullable = false)
    private String matchId;

    @Column(name = "market_id",length = 50,nullable = false)
    private String marketId;

    @Column(name = "bet_name")
    private String betName;

    @Column(name = "price",length = 50,nullable = false,columnDefinition = "varchar(50) default '0'")
    private String price;

    @Column(name = "base_line",columnDefinition = "varchar(50) default '0'")
    private String baseLine;

    @Column(name = "settlement")
    private String settlement;

    @Column(name = "bet_status",length = 50)
    private String betStatus;

    @Column(name = "pre_total_amount", length = 13, nullable = false,columnDefinition = "varchar(13)  default '0'")
    private String preTotalAmount;
}
