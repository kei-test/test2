package GInternational.server.amzn.dto.total;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
public class AmznPartnerResDTO {
    private Long id;
    private String username;
    private String nickname;
    private long amazonMoney;
    private long amazonMileage;
    private int recommendedCount;
    private String partnerType;
    private LocalDateTime createdAt;
    private Long daeId;
    private Long bonId;
    private Long buId;
    private Long chongId;
    private Long maeId;


    @QueryProjection
    public AmznPartnerResDTO(Long id, String username, String nickname, long amazonMoney, long amazonMileage, int recommendedCount, String partnerType, LocalDateTime createdAt, Long daeId, Long bonId, Long buId, Long chongId, Long maeId) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.amazonMoney = amazonMoney;
        this.amazonMileage = amazonMileage;
        this.recommendedCount = recommendedCount;
        this.partnerType = partnerType;
        this.createdAt = createdAt;
        this.daeId = daeId;
        this.bonId = bonId;
        this.buId = buId;
        this.chongId = chongId;
        this.maeId = maeId;
    }
}
