package GInternational.server.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LevelAccountSettingReqDTO {

    private int lv;
    private String bankName;      // 은행명
    private String accountNumber; // 계좌번호
    private String ownerName;     // 예금주
    private String csNumber;      // 고객센터번호
}
