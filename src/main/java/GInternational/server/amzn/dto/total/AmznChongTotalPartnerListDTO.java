package GInternational.server.amzn.dto.total;


import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class AmznChongTotalPartnerListDTO {

    private Long id;
    private String username;
    private String nickname;
    private long amazonMoney;
    private long amazonMileage;
    private int recommendedCount;
    private String rate;
    private LocalDateTime createdAt;
    private Long num;
    private List<AmznMaePartnerResDTO> maeList = new ArrayList<>();
}
