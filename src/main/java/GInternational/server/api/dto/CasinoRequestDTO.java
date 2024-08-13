package GInternational.server.api.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CasinoRequestDTO {

    private long exchangeCasinoBalance;  //전환 신청 금액(신청하는 카지노머니 금액)  카 -> 스
    private long exchangeSportsBalance;  //전환 신청 금액(신청하는 스포츠머니 금액)  스 -> 카


}
