package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PointResponseDTO {

    //충전 후 유저에게 리턴
    private long sportsBalance;  //포인트 전환 후 현재 스포츠머니

}
