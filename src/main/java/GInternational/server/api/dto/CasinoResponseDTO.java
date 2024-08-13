package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CasinoResponseDTO {

    //충전 후 유저에게 리턴
    private long sportsBalance;  //전환 후 현재 스포츠
    private long casinoBalance;  //전환 후 카지노 머니
}
