package GInternational.server.amzn.dto.total;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@NoArgsConstructor
@Getter
@Setter
public class AmznTotalPartnerReqDTO {

    //하부 총판 조회
    private Long id;
    private String username;
    private String nickname;
    private long amazonMoney;
    private long amazonMileage;
    private int recommendedCount;
    private String partnerType;
    private Long daeId;
    private Long bonId;
    private Long buId;
    private Long chongId;
    private LocalDateTime createdAt;


    @QueryProjection
    public AmznTotalPartnerReqDTO(Long id, String username, String nickname, long amazonMoney, long amazonMileage, int recommendedCount, String partnerType, Long daeId, Long bonId, Long buId, Long chongId, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.amazonMoney = amazonMoney;
        this.amazonMileage = amazonMileage;
        this.recommendedCount = recommendedCount;
        this.partnerType = partnerType;
        this.daeId = daeId;
        this.bonId = bonId;
        this.buId = buId;
        this.chongId = chongId;
        this.createdAt = createdAt;
    }
}